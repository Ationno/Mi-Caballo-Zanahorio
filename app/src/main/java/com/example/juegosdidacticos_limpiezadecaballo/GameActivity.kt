package com.example.juegosdidacticos_limpiezadecaballo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.RectF
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.ErrorType
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigGameEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.GamePageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.GameViewModel
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.BackgroundMusicPlayer
import kotlinx.coroutines.launch
import java.util.Date

class GameActivity : AppCompatActivity() {

    private lateinit var binding: GamePageBinding
    private val userViewModel: UserViewModel by viewModels()
    private val gameViewModel: GameViewModel by viewModels()
    private var user: PatientEntity? = null
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var totalTimeInMillis: Long = 0
    private var difficulty: Difficulty = Difficulty.EASY
    private var errors: Int = 0
    private var score: Int = 0
    private var gameVolume: Int = 0
    private var voiceVolume: Int = 0
    private var musicVolume: Int = 0
    private var errorsOnCurrentStep = 0
    private var currentStep = 0
    private val dirtyOverlays = mutableMapOf<String, ImageView>()

    private val cleaningOrder = listOf(
        Pair("head", "soft_scraper"),
        Pair("neck", "hard_scraper"),
        Pair("shoulder", "hard_scraper"),
        Pair("back", "hard_scraper"),
        Pair("belly", "hard_scraper"),
        Pair("haunch", "hard_scraper"),
        Pair("front_legs", "soft_scraper"),
        Pair("hind_legs", "soft_scraper"),
        Pair("groin", "hard_scraper"),
        Pair("whole_body", "soft_brush"),
        Pair("main", "hard_brush"),
        Pair("tail", "hard_brush"),
        Pair("hooves_1", "hoof_pick"),
        Pair("hooves_2", "hoof_pick"),
        Pair("hooves_3", "hoof_pick")
    )

    private val horseRegionsDp = mutableMapOf(
        "head" to Pair(RectF(30f, 5f, 105f, 120f), true),
        "neck" to Pair(RectF(95f, 20f, 160f, 100f), false),
        "shoulder" to Pair(RectF(115f, 150f, 160f, 190f), false),
        "back" to Pair(RectF(160f, 95f, 250f, 115f), false),
        "belly" to Pair(RectF(170f, 170f, 235f, 210f), false),
        "haunch" to Pair(RectF(251f, 95f, 300f, 140f), false),
        "front_legs" to Pair(RectF(30f, 191f, 150f, 330f), false),
        "hind_legs" to Pair(RectF(236f, 201f, 300f, 330f), false),
        "groin" to Pair(RectF(236f, 171f, 270f, 200f), false),
        "whole_body" to Pair(RectF(70f, 100f, 300f, 175f), false),
        "main" to Pair(RectF(95f, 20f, 170f, 115f), false),
        "tail" to Pair(RectF(280f, 100f, 330f, 290f), false),
        "hooves_1" to Pair(RectF(90f, 315f, 115f, 330f), false),
        "hooves_2" to Pair(RectF(220f, 310f, 245f, 325f), false),
        "hooves_3" to Pair(RectF(280f, 310f, 300f, 325f), false)
    )

    private val clues = mapOf(
        0 to Pair(
            "Pista: La cabeza es sensible, es mejor usar herramientas que no duelan!.",
            "Pista: La rasqueta suave se utiliza para limpiar la cabeza."
        ),
        1 to Pair(
            "Pista: El cuello es difícil de limpiar, es mejor usar herramientas fuertes!.",
            "Pista: La rasqueta dura se utiliza para limpiar el cuello."
        ),
        2 to Pair(
            "Pista: La paleta necesita una buena limpieza, las herramientas más resistentes son mejores!.",
            "Pista: La rasqueta dura se utiliza para limpiar la paleta."
        ),
        3 to Pair(
            "Pista: Los caballos tienen jinetes en su lomo constantemente, es mejor limpiarlo con herramientas fuertes!.",
            "Pista: La rasqueta dura se utiliza para limpiar el lomo."
        ),
        4 to Pair(
            "Pista: La panza de los caballos suele estar bastante sucia, hay que limpiarla con mucha dureza!",
            "Pista: La rasqueta dura se utiliza para limpiar la panza."
        ),
        5 to Pair(
            "Pista: La anca debe limpiarse con firmeza, una herramienta resistente es la mejor opción!",
            "Pista: La rasqueta dura se utiliza para limpiar la anca."
        ),
        6 to Pair(
            "Pista: Las manos del caballo son delicadas, es mejor usar algo suave para limpiarlas.",
            "Pista: La rasqueta blanda se utiliza para limpiar las manos."
        ),
        7 to Pair(
            "Pista: Las patas deben mantenerse limpias, pero hay que hacerlo con cuidado.",
            "Pista: La rasqueta blanda se utiliza para limpiar las patas."
        ),
        8 to Pair(
            "Pista: La verija es una zona difícil de limpiar, se necesita una herramienta fuerte!",
            "Pista: La rasqueta dura se utiliza para limpiar la verija."
        ),
        9 to Pair(
            "Pista: Para un buen mantenimiento, el cuerpo en general necesita un cepillado suave.",
            "Pista: El cepillo blando se utiliza para limpiar el cuerpo en general."
        ),
        10 to Pair(
            "Pista: Las crines pueden enredarse fácilmente, es mejor usar un cepillo firme!",
            "Pista: El cepillo duro se utiliza para limpiar las crines."
        ),
        11 to Pair(
            "Pista: La cola puede acumular suciedad y enredos, se necesita un buen cepillado!",
            "Pista: El cepillo duro se utiliza para limpiar la cola."
        ),
        12 to Pair(
            "Pista: El vaso de la mano izquierda delantera debe mantenerse limpio para evitar problemas.",
            "Pista: El escarbavasos se utiliza para limpiar el vaso de la mano izquierda delantera."
        ),
        13 to Pair(
            "Pista: El vaso de la pata derecha delantera también necesita una limpieza profunda.",
            "Pista: El escarbavasos se utiliza para limpiar el vaso de la pata derecha delantera."
        ),
        14 to Pair(
            "Pista: El vaso de la pata izquierda delantera no debe descuidarse, es importante limpiarlo bien.",
            "Pista: El escarbavasos se utiliza para limpiar el vaso de la pata izquierda delantera."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GamePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!BackgroundMusicPlayer.isPlaying()) {
            BackgroundMusicPlayer.start(this, R.raw.game_music)
        }

        BackgroundMusicPlayer.changeMusic(this, R.raw.game_music)

        user = intent.getParcelableExtra("USER", PatientEntity::class.java)
        val patientId = user?.id

        patientId?.let { id: Int ->
            lifecycleScope.launch {
                val config = userViewModel.getConfigByPatientId(id)
                config?.let {
                    initializeDifficulty(it.difficulty)
                    startTimer()
                }
            }
        }

        setupClickListeners()
        setupDragAndDrop()

        dirtyOverlays["head"] = binding.dirtyHead
        dirtyOverlays["neck"] = binding.dirtyNeck
        dirtyOverlays["shoulder"] = binding.dirtyShoulder
        dirtyOverlays["back"] = binding.dirtyBack
        dirtyOverlays["belly"] = binding.dirtyBelly
        dirtyOverlays["haunch"] = binding.dirtyHaunch
        dirtyOverlays["front_legs"] = binding.dirtyFrontLegs
        dirtyOverlays["hind_legs"] = binding.dirtyHindLegs
        dirtyOverlays["groin"] = binding.dirtyGroin
        dirtyOverlays["whole_body"] = binding.dirtyWholeBody
        dirtyOverlays["main"] = binding.dirtyMain
        dirtyOverlays["tail"] = binding.dirtyTail
        dirtyOverlays["hooves_1"] = binding.dirtyHooves1
        dirtyOverlays["hooves_2"] = binding.dirtyHooves2
        dirtyOverlays["hooves_3"] = binding.dirtyHooves3

        for ((region, pair) in horseRegionsDp) {
            val (rectF, _) = pair
            val overlay = dirtyOverlays[region]

            if (overlay != null) {
                val left = dpToPx(rectF.left)
                val top = dpToPx(rectF.top)
                val right = dpToPx(rectF.right)
                val bottom = dpToPx(rectF.bottom)

                overlay.layoutParams = FrameLayout.LayoutParams(
                    (right - left).toInt(),
                    (bottom - top).toInt()
                )
                overlay.x = left
                overlay.y = top
                overlay.visibility = View.VISIBLE
            }
        }

        user?.id?.let { id: Int ->
            lifecycleScope.launch {
                val configGame = userViewModel.getGameConfigByPatientId(id)!!
                gameVolume = configGame.gameVolume
                voiceVolume = configGame.voiceVolume
                musicVolume = configGame.musicVolume
                BackgroundMusicPlayer.setVolume(musicVolume, gameVolume)
            }
        }
    }

    private fun updateDirtyOverlays(currentPart: String) {
        val overlay = dirtyOverlays[currentPart]
        if (overlay != null) {
            overlay.visibility = View.GONE
        }
    }

    private fun initializeDifficulty(difficulty: Difficulty) {
        this.difficulty = difficulty
        when (difficulty) {
            Difficulty.EASY -> {
                timeLeftInMillis = 480000L
                totalTimeInMillis = 480000L
            }

            Difficulty.MEDIUM -> {
                timeLeftInMillis = 240000L
                totalTimeInMillis = 240000L
            }

            Difficulty.HARD -> {
                timeLeftInMillis = 120000L
                totalTimeInMillis = 120000L

            }
        }
        binding.errorValue.text = buildString {
            append("0/")
            append(getMaxErrors().toString())
        }
        binding.topMenuDifficulty.text = difficulty.getDisplayDifficulty()
    }

    private fun getMaxErrors(): Int {
        return when (difficulty) {
            Difficulty.EASY -> 24
            Difficulty.MEDIUM -> 16
            Difficulty.HARD -> 8
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                binding.totalTime.text = formatTime(timeLeftInMillis)
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                binding.totalTime.text = formatTime(timeLeftInMillis)
                endGame(false)
            }
        }.start()
    }

    private fun formatTime(time: Long): String {
        val minutes = (time / 1000) / 60
        val seconds = (time / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun endGame(completed: Boolean) {
        countDownTimer?.cancel()

        MediaPlayer.create(this, if (completed) R.raw.win_game else R.raw.game_over).apply {
            setVolume(gameVolume / 100f, gameVolume / 100f)
            start()
            setOnCompletionListener {
                release()
            }
        }

        val loseReason = when {
            !completed && timeLeftInMillis <= 0 -> "Se te ha acabado el tiempo!"
            !completed && errors >= getMaxErrors() -> "Demasiados errores!"
            else -> null
        }

        showGameResultDialog(completed, loseReason)
        saveGameState(completed)
    }

    private fun saveGameState(completed: Boolean) {
        val gameState = user?.id?.let {
            GameStateEntity(
                date = Date(),
                errors = errors,
                score = score,
                difficulty = difficulty,
                timePlayed = totalTimeInMillis - timeLeftInMillis,
                patientId = it
            )
        }

        lifecycleScope.launch {
            if (gameState != null) {
                gameViewModel.insertGameState(gameState)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnPause.setOnClickListener {
            onPauseButtonClicked()
        }

        binding.btnSettings.setOnClickListener {
            onSettingsButtonClicked()
        }

        binding.btnHint.setOnClickListener {
            onHintButtonClicked()
        }
    }

    private fun onPauseButtonClicked() {
        pauseTimer()

        val dialogView = layoutInflater.inflate(R.layout.pause_page, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.menuButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("SHOW_USER_INIT_FRAGMENT", true)
                putExtra("USER_DATA", user)
            }
            startActivity(intent)
            dialog.dismiss()
            finish()
        }

        dialogView.findViewById<View>(R.id.restartButton).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("USER", user)
            }
            startActivity(intent)
            dialog.dismiss()
            finish()
        }

        dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.resumeButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            resumeTimer()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val widthInDp = 410
        val widthInPixels = (widthInDp * resources.displayMetrics.density).toInt()

        dialog.window?.apply {
            setLayout(widthInPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun onSettingsButtonClicked() {
        pauseTimer()

        val dialogView = layoutInflater.inflate(R.layout.config_game_page, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val gameVolumeSeekBar = dialogView.findViewById<SeekBar>(R.id.gameVolumeSeekBar)
        val gameVolumePercentage = dialogView.findViewById<TextView>(R.id.gameVolumePercentage)
        val voiceVolumeSeekBar = dialogView.findViewById<SeekBar>(R.id.voiceVolumeSeekBar)
        val voiceVolumePercentage = dialogView.findViewById<TextView>(R.id.voiceVolumePercentage)
        val musicVolumeSeekBar = dialogView.findViewById<SeekBar>(R.id.musicVolumeSeekBar)
        val musicVolumePercentage = dialogView.findViewById<TextView>(R.id.musicVolumePercentage)

        user?.id?.let { id: Int ->
            lifecycleScope.launch {
                val configGame = userViewModel.getGameConfigByPatientId(id)!!
                gameVolume = configGame.gameVolume
                voiceVolume = configGame.voiceVolume
                musicVolume = configGame.musicVolume
            }
        }

        gameVolumeSeekBar.progress = gameVolume
        gameVolumePercentage.text = "$gameVolume%"

        voiceVolumeSeekBar.progress = voiceVolume
        voiceVolumePercentage.text = "$voiceVolume%"

        musicVolumeSeekBar.progress = musicVolume
        musicVolumePercentage.text = "$musicVolume%"

        gameVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gameVolumePercentage.text = "$progress%"
                BackgroundMusicPlayer.setVolume(musicVolumeSeekBar.progress, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        voiceVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voiceVolumePercentage.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        musicVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                musicVolumePercentage.text = "$progress%"
                BackgroundMusicPlayer.setVolume(progress, gameVolumeSeekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        dialogView.findViewById<View>(R.id.confirmButton).setOnClickListener {
            val newGameVolume = gameVolumeSeekBar.progress
            val newVoiceVolume = voiceVolumeSeekBar.progress
            val newMusicVolume = musicVolumeSeekBar.progress

            gameVolume = newGameVolume
            voiceVolume = newVoiceVolume
            musicVolume = newMusicVolume

            user?.id?.let { id: Int ->
                lifecycleScope.launch {
                    userViewModel.updateGameConfig(
                        ConfigGameEntity(
                            patientId = id,
                            gameVolume = gameVolume,
                            voiceVolume = voiceVolume,
                            musicVolume = musicVolume
                        )
                    )
                }
            }

            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
            BackgroundMusicPlayer.setVolume(musicVolume, gameVolume)
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            BackgroundMusicPlayer.setVolume(musicVolume, gameVolume)
            resumeTimer()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val widthInDp = 410
        val widthInPixels = (widthInDp * resources.displayMetrics.density).toInt()
        dialog.window?.setLayout(widthInPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun onHintButtonClicked() {
        if (errorsOnCurrentStep >= 4) {
            val clue = when {
                errorsOnCurrentStep >= 8 -> clues[currentStep]?.second
                else -> clues[currentStep]?.first
            }

            if (clue != null) {
                binding.clueText.text = clue
                binding.clueText.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.clueText.visibility = View.GONE
                }, 5000)
            }
        }
    }

    private fun setupDragAndDrop() {
        val tools = listOf(
            binding.hardScraper,
            binding.softScraper,
            binding.hoofPick,
            binding.softBrush,
            binding.hardBrush
        )

        tools.forEach { tool ->
            tool.setOnLongClickListener { v ->
                val clipData = ClipData.newPlainText("tool", v.id.toString())
                val dragShadowBuilder = View.DragShadowBuilder(v)
                v.startDragAndDrop(clipData, dragShadowBuilder, v, 0)
                true
            }
        }

        var success: Boolean = false

        binding.horseImage.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    success = false
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    val tool = event.localState as ImageView
                    val toolName = tool.id
                    val horsePart = getHorsePartUnderDrag(event.x, event.y)

                    if (horsePart != null && isCorrectToolForPart(toolName, horsePart)) {
                        val overlay = dirtyOverlays[horsePart]
                        if (overlay != null && overlay.visibility == View.VISIBLE) {
                            overlay.alpha -= 0.01f
                            if (overlay.alpha <= 0f) {
                                overlay.visibility = View.GONE
                                playSuccessEffect()
                                updateScore()
                                updateProgressBar()
                                moveRider()
                                success = true
                            }
                        }
                    }
                    true
                }

                DragEvent.ACTION_DROP -> {
                    val tool = event.localState as ImageView
                    val toolName = tool.id
                    val horsePart = getHorsePartUnderDrag(event.x, event.y)

                    if ((horsePart == null || !isCorrectToolForPart(
                            toolName,
                            horsePart
                        )) && !success
                    ) {
                        playErrorEffect()
                        incrementErrors()
                        if (horsePart == null) {
                            showErrorAlert(ErrorType.INCORRECT_PART)
                        } else {
                            showErrorAlert(ErrorType.INCORRECT_TOOL)
                        }
                    }
                    true
                }

                else -> true
            }
        }
    }

    private fun moveRider() {
        val imageView = binding.rider
        val layoutParams = imageView.layoutParams as FrameLayout.LayoutParams

        when (currentStep) {
            1 -> {
                layoutParams.marginStart = -dpToPx(150f).toInt()
            }

            2 -> {
                layoutParams.marginStart = -dpToPx(130f).toInt()
            }

            3 -> {
                layoutParams.marginStart = -dpToPx(70f).toInt()
            }

            4 -> {
                layoutParams.marginStart = -dpToPx(80f).toInt()
                changeRiderImage(imageView, R.drawable.rider_squat)
            }

            5 -> {
                layoutParams.marginStart = -dpToPx(10f).toInt()
                changeRiderImage(imageView, R.drawable.rider)
            }

            6 -> {
                layoutParams.marginStart = -dpToPx(170f).toInt()
                changeRiderImage(imageView, R.drawable.rider_squat)
            }

            7 -> {
                layoutParams.marginStart = 0
            }

            8 -> {
                layoutParams.marginStart = -dpToPx(20f).toInt()
            }

            9 -> {
                layoutParams.marginStart = -dpToPx(100f).toInt()
                changeRiderImage(imageView, R.drawable.rider)
            }

            10 -> {
                layoutParams.marginStart = -dpToPx(130f).toInt()
            }

            11 -> {
                layoutParams.marginStart = dpToPx(40f).toInt()
            }

            12 -> {
                layoutParams.marginStart = -dpToPx(140f).toInt()
                changeRiderImage(imageView, R.drawable.rider_squat)
            }

            13 -> {
                layoutParams.marginStart = -dpToPx(20f).toInt()
            }

            14 -> {
                layoutParams.marginStart = dpToPx(50f).toInt()
            }

            else -> {
                layoutParams.marginStart = layoutParams.marginStart
            }
        }

        imageView.layoutParams = layoutParams
        imageView.post {
            imageView.requestLayout()
            (imageView.parent as ViewGroup).invalidate()
        }
    }

    private fun changeRiderImage(imageView: ImageView, newImageRes: Int) {
        val initialAlpha = imageView.alpha

        val fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", initialAlpha, 0f)
        fadeOut.duration = 300

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                imageView.setImageResource(newImageRes)
                imageView.alpha = 0f

                val fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, initialAlpha)
                fadeIn.duration = 300
                fadeIn.start()
            }
        })

        fadeOut.start()
    }


    private fun showErrorAlert(errorType: ErrorType) {
        binding.errorAlert.text = errorType.getInfo()
        binding.errorAlert.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.errorAlert.visibility = View.GONE
        }, 3000)
    }

    private fun isCleaningMotion(deltaX: Float, deltaY: Float): Boolean {
        val isTopToBottom = deltaY > 0
        val isRightToLeft = deltaX < 0

        return isTopToBottom || isRightToLeft
    }

    private fun isCleaningMotionReversed(deltaX: Float, deltaY: Float): Boolean {
        val isTopToBottom = deltaY < 5
        val isRightToLeft = deltaX > 0

        return isTopToBottom || isRightToLeft
    }


    private fun getHorsePartUnderDrag(x: Float, y: Float): String? {

        val accessibleHorseRegionsPx = horseRegionsDp
            .filter { (_, pair) -> pair.second }
            .mapValues { (_, pair) ->
                RectF(
                    dpToPx(pair.first.left),
                    dpToPx(pair.first.top),
                    dpToPx(pair.first.right),
                    dpToPx(pair.first.bottom)
                )
            }

        return accessibleHorseRegionsPx.entries.find { (_, rect) -> rect.contains(x, y) }?.key
    }

    private fun Context.dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun isCorrectToolForPart(toolId: Int, horsePart: String?): Boolean {
        if (currentStep >= cleaningOrder.size) return false

        val (expectedPart, expectedToolName) = cleaningOrder[currentStep]

        val toolName = when (toolId) {
            R.id.hardScraper -> "hard_scraper"
            R.id.softScraper -> "soft_scraper"
            R.id.hoofPick -> "hoof_pick"
            R.id.softBrush -> "soft_brush"
            R.id.hardBrush -> "hard_brush"
            else -> return false
        }

        return horsePart == expectedPart && toolName == expectedToolName
    }

    private fun playSuccessEffect() {
        MediaPlayer.create(this, R.raw.relincho).apply {
            setVolume(gameVolume / 100f, gameVolume / 100f)
            start()
            setOnCompletionListener {
                release()
            }
        }

        binding.greenOverlay.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.greenOverlay.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.greenOverlay.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.greenOverlay.visibility = View.GONE
                }, 200)
            }, 200)
        }, 200)

        val currentPart = cleaningOrder[currentStep].first
        val currentPartPair = horseRegionsDp[currentPart]
        if (currentPartPair != null) {
            horseRegionsDp[currentPart] = currentPartPair.copy(second = false)
        }

        updateDirtyOverlays(currentPart)

        currentStep++

        if (currentStep < cleaningOrder.size) {
            val nextPart = cleaningOrder[currentStep].first
            val nextPartPair = horseRegionsDp[nextPart]
            if (nextPartPair != null) {
                horseRegionsDp[nextPart] = nextPartPair.copy(second = true)
            }
        }

        errorsOnCurrentStep = 0
        binding.btnHint.alpha = 0.5f
        binding.clueText.visibility = View.GONE
    }

    private fun playErrorEffect() {
        MediaPlayer.create(this, R.raw.resoplido).apply {
            setVolume(gameVolume / 100f, gameVolume / 100f)
            start()
            setOnCompletionListener {
                release()
            }
        }

        binding.redOverlay.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.redOverlay.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.redOverlay.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.redOverlay.visibility = View.GONE
                }, 200)
            }, 200)
        }, 200)
    }

    private fun updateScore() {
        val baseScore = 10

        val difficultyMultiplier = when (difficulty) {
            Difficulty.EASY -> 1.0
            Difficulty.MEDIUM -> 1.5
            Difficulty.HARD -> 2.0
        }

        val timeSpent = totalTimeInMillis - timeLeftInMillis
        val timeMultiplier = when {
            timeSpent < totalTimeInMillis * 0.25 -> 1.5
            timeSpent < totalTimeInMillis * 0.5 -> 1.2
            else -> 1.0
        }

        val errorMultiplier = when (errorsOnCurrentStep) {
            0 -> 2.0
            1 -> 1.5
            2 -> 1.2
            3 -> 1.0
            else -> 0.8
        }

        val stepScore =
            (baseScore * difficultyMultiplier * timeMultiplier * errorMultiplier).toInt()

        score += stepScore

        binding.scoreValue.text = score.toString()
    }

    private fun incrementErrors() {
        errors++
        errorsOnCurrentStep++

        if (errors > getMaxErrors()) {
            endGame(false)
            return
        }

        val currentText = binding.errorValue.text.toString()
        val updatedText = currentText.replace(Regex("^\\d+"), errors.toString())
        binding.errorValue.text = updatedText

        if (errorsOnCurrentStep % 4 == 0) {
            binding.btnHint.alpha = 1.0f
            startTwinklingEffect()
        }
    }

    private fun startTwinklingEffect() {
        val handler = Handler(Looper.getMainLooper())
        val twinkle = object : Runnable {
            override fun run() {
                if (errorsOnCurrentStep >= 4) {
                    binding.btnHint.alpha = if (binding.btnHint.alpha == 1.0f) 0.5f else 1.0f
                    handler.postDelayed(this, 500)
                }
            }
        }
        handler.post(twinkle)
    }

    private fun updateProgressBar() {
        val progress = (currentStep.toFloat() / cleaningOrder.size) * 100
        binding.progressBar.progress = progress.toInt()
        if (progress == 100f) {
            endGame(true)
            return
        }
    }

    private fun showGameResultDialog(isWin: Boolean, loseReason: String?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_game_result, null)

        val title = dialogView.findViewById<TextView>(R.id.titleGameResult)
        val info = dialogView.findViewById<TextView>(R.id.infoGameResult)
        val timePlayed = dialogView.findViewById<TextView>(R.id.timePlayed)
        val errorsText = dialogView.findViewById<TextView>(R.id.errors)
        val difficultyText = dialogView.findViewById<TextView>(R.id.difficulty)
        val points = dialogView.findViewById<TextView>(R.id.points)
        val progress = dialogView.findViewById<TextView>(R.id.progress)

        if (isWin) {
            title.text = "Has Ganado!"
        } else {
            title.text = "Has Perdido!"
            if (loseReason != null) {
                info.visibility = View.VISIBLE
                info.text = "$loseReason"
            }
        }

        timePlayed.text = "Tiempo jugado: ${formatTime(totalTimeInMillis - timeLeftInMillis)}"
        errorsText.text = buildString {
            append("Errores: $errors/")
            append(getMaxErrors().toString())
        }
        difficultyText.text = "Dificultad: ${difficulty.getDisplayDifficulty()}"
        points.text = "Puntos: $score"
        progress.text = "Progreso: ${(currentStep.toFloat() / cleaningOrder.size * 100).toInt()}%"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<View>(R.id.closeButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("SHOW_USER_INIT_FRAGMENT", true)
                putExtra("USER_DATA", user)
            }
            startActivity(intent)
            dialog.dismiss()
            finish()
        }

        dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("SHOW_USER_INIT_FRAGMENT", true)
                putExtra("USER_DATA", user)
            }
            startActivity(intent)
            dialog.dismiss()
            finish()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
    }
}