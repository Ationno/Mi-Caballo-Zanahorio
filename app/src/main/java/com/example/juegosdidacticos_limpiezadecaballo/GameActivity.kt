package com.example.juegosdidacticos_limpiezadecaballo

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
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.GamePageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.GameViewModel
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.BackgroundMusicPlayer
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var binding: GamePageBinding
    private val userViewModel: UserViewModel by viewModels()
    private val gameViewModel: GameViewModel by viewModels()
    private var user: PacientEntity? = null
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var totalTimeInMillis: Long = 0
    private var difficulty: Difficulty = Difficulty.EASY
    private var errors: Int = 0
    private var score: Int = 0
    private var gameVolume: Int = 0
    private var voiceVolume: Int = 0

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

    private var currentStep = 0

    private val dirtyOverlays = mutableMapOf<String, ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GamePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!BackgroundMusicPlayer.isPlaying()) {
            BackgroundMusicPlayer.start(this, R.raw.game_music)
        }

        BackgroundMusicPlayer.changeMusic(this, R.raw.game_music)

        user = intent.getParcelableExtra("user", PacientEntity::class.java)
        val pacientId = user?.id

        pacientId?.let { id: Int ->
            lifecycleScope.launch {
                val config = userViewModel.getConfigByPacientId(id)
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

        val sharedPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        gameVolume = sharedPrefs.getInt("gameVolume", 50)
        voiceVolume = sharedPrefs.getInt("voiceVolume", 50)
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

                binding.errorValue.text = "0/24"
            }
            Difficulty.MEDIUM -> {
                timeLeftInMillis = 240000L
                totalTimeInMillis = 240000L

                binding.errorValue.text = "0/16"
            }
            Difficulty.HARD -> {
                timeLeftInMillis = 120000L
                totalTimeInMillis = 120000L

                binding.errorValue.text = "0/8"
            }
        }
        binding.totalTime.text = (timeLeftInMillis / 1000).toString()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimer()
                endGame(false)
            }
        }.start()
    }

    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.totalTime.text = timeFormatted
    }

    private fun pauseTimer() {
        countDownTimer?.cancel() // Stop the timer
    }

    private fun resumeTimer() {
        startTimer() // Restart the timer with the remaining time
    }

    private fun endGame(completed: Boolean) {
        countDownTimer?.cancel()
        saveGameState(completed)
        Toast.makeText(this, if (completed) "Game Completed!" else "Time's Up!", Toast.LENGTH_SHORT).show()
    }

    private fun saveGameState(completed: Boolean) {
        val gameState = GameStateEntity(
            errors = errors,
            score = score,
            difficulty = difficulty,
            timeLeft = timeLeftInMillis,
            timeTotal = totalTimeInMillis
        )
        lifecycleScope.launch {
            gameViewModel.insertGameState(gameState)
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

        /* binding.horseImage.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x // x coordinate in pixels
                val y = event.y // y coordinate in pixels

                // Convert pixels to dp
                val xDp = pxToDp(x)
                val yDp = pxToDp(y)

                // Log the coordinates in dp
                Log.d("HorseImageClick", "Clicked at (x: $xDp dp, y: $yDp dp)")
            }
            true // Return true to indicate the event was handled
        }
        */
    }

    private fun pxToDp(px: Float): Float {
        return px / resources.displayMetrics.density
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
            finish()
            dialog.dismiss()
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

        val sharedPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        gameVolume = sharedPrefs.getInt("gameVolume", 50)
        voiceVolume = sharedPrefs.getInt("voiceVolume", 50)

        gameVolumeSeekBar.progress = gameVolume
        gameVolumePercentage.text = "$gameVolume%"

        voiceVolumeSeekBar.progress = voiceVolume
        voiceVolumePercentage.text = "$voiceVolume%"

        gameVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gameVolumePercentage.text = "$progress%"
                BackgroundMusicPlayer.setVolume(progress)
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

        dialogView.findViewById<View>(R.id.confirmButton).setOnClickListener {
            val newGameVolume = gameVolumeSeekBar.progress
            val newVoiceVolume = voiceVolumeSeekBar.progress
            gameVolume = newGameVolume
            voiceVolume = newVoiceVolume

            sharedPrefs.edit()
                .putInt("gameVolume", newGameVolume)
                .putInt("voiceVolume", newVoiceVolume)
                .apply()

            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
            BackgroundMusicPlayer.setVolume(gameVolume)
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            BackgroundMusicPlayer.setVolume(gameVolume)
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
        // Implement hint functionality
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
                val clipData = ClipData.newPlainText("tool", v.id.toString()) // Use ID instead of contentDescription
                val dragShadowBuilder = View.DragShadowBuilder(v)
                v.startDragAndDrop(clipData, dragShadowBuilder, v, 0)
                true
            }
        }

        var dragStartX = 0f
        var dragStartY = 0f
        var previousX = 0f
        var previousY = 0f
        var isCleaning = false
        var countCleaning = 0
        var countCleaningReversed = 0

        binding.horseImage.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    dragStartX = event.x
                    dragStartY = event.y
                    previousX = dragStartX
                    previousY = dragStartY
                    isCleaning = false
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    val currentX = event.x
                    val currentY = event.y

                    val deltaX = currentX - previousX
                    val deltaY = currentY - previousY

                    if (isCleaningMotion(deltaX, deltaY)) {
                        countCleaning += 1
                    } else if (isCleaningMotionReversed(deltaX, deltaY)) {
                        countCleaningReversed += 1
                    }
                    previousX = currentX
                    previousY = currentY
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val tool = event.localState as ImageView
                    val toolName = tool.id
                    val horsePart = getHorsePartUnderDrag(event.x, event.y)

                    Log.d("CountCleaning", "Count cleaning: $countCleaning")
                    Log.d("CountCleaningReversed", "Count cleaning reversed: $countCleaningReversed")

                    isCleaning = countCleaning > countCleaningReversed && countCleaning > 50 && countCleaningReversed < 20

                    countCleaning = 0
                    countCleaningReversed = 0

                    Log.d("DragDrop", "Cleaning motion: $isCleaning")
                    Log.d("ToolName", "Tool name: $toolName")
                    Log.d("HorsePart", "Horse part: $horsePart")

                    if (horsePart != null && isCorrectToolForPart(toolName, horsePart)) {
                        if (isCleaning) {
                            playSuccessEffect()
                            updateScore()
                            Log.d("DragDrop", "Cleaning motion completed for $horsePart")
                        } else {
                            playErrorEffect()
                            incrementErrors()
                            Log.d("DragDrop", "No cleaning motion for $horsePart")
                        }
                    } else {
                        playErrorEffect()
                        incrementErrors()
                        Log.d("DragDrop", "Incorrect tool or part")
                    }
                    true
                }
                else -> true
            }
        }
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

    fun Context.dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun isCorrectToolForPart(toolId: Int, horsePart: String?): Boolean {
        if (currentStep >= cleaningOrder.size) return false

        val (expectedPart, expectedToolName) = cleaningOrder[currentStep]

        val toolName = when (toolId) {
            R.id.hard_scraper -> "hard_scraper"
            R.id.soft_scraper -> "soft_scraper"
            R.id.hoof_pick -> "hoof_pick"
            R.id.soft_brush -> "soft_brush"
            R.id.hard_brush -> "hard_brush"
            else -> return false
        }

        return horsePart == expectedPart && toolName == expectedToolName
    }

    private fun playSuccessEffect() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.relincho)
        mediaPlayer.setVolume(gameVolume / 100f, gameVolume / 100f)
        mediaPlayer.start()

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

        updateProgressBar()
    }

    private fun playErrorEffect() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.resoplido)
        mediaPlayer.setVolume(gameVolume / 100f, gameVolume / 100f)
        mediaPlayer.start()

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
        score += 10
        binding.scoreValue.text = score.toString()
    }

    private fun incrementErrors() {
        errors++
        val currentText = binding.errorValue.text.toString()
        val updatedText = currentText.replace(Regex("^\\d+"), errors.toString())
        binding.errorValue.text = updatedText
    }

    private fun updateProgressBar() {
        val progress = (currentStep.toFloat() / cleaningOrder.size) * 100
        binding.progressBar.progress = progress.toInt()
    }
}