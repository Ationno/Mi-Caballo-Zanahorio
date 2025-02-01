package com.example.juegosdidacticos_limpiezadecaballo

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.GamePageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.GameViewModel
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GamePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the difficulty and time based on the selected difficulty
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
        Log.d("Timer", "Time left: $timeFormatted")
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
        val gameVolume = sharedPrefs.getInt("gameVolume", 50)
        val voiceVolume = sharedPrefs.getInt("voiceVolume", 50)

        gameVolumeSeekBar.progress = gameVolume
        gameVolumePercentage.text = "$gameVolume%"

        voiceVolumeSeekBar.progress = voiceVolume
        voiceVolumePercentage.text = "$voiceVolume%"

        gameVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gameVolumePercentage.text = "$progress%"
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

            sharedPrefs.edit()
                .putInt("gameVolume", newGameVolume)
                .putInt("voiceVolume", newVoiceVolume)
                .apply()

            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
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
                val clipData = ClipData.newPlainText("tool", v.contentDescription)
                val dragShadowBuilder = View.DragShadowBuilder(v)
                v.startDragAndDrop(clipData, dragShadowBuilder, v, 0)
                true
            }
        }

        binding.horseImage.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val tool = event.localState as ImageView
                    val toolName = tool.contentDescription.toString()
                    val horsePart = getHorsePartUnderDrag(event.x, event.y)
                    if (isCorrectToolForPart(toolName, horsePart)) {
                        // Correct tool for the part
                        playSuccessEffect()
                        updateScore()
                    } else {
                        // Incorrect tool for the part
                        playErrorEffect()
                        incrementErrors()
                    }
                    true
                }
                else -> true
            }
        }
    }

    private fun getHorsePartUnderDrag(x: Float, y: Float): String? {
        val horseRegionsDp = mapOf(
            "head" to RectF(50f, 100f, 200f, 250f),
            "neck" to RectF(200f, 250f, 350f, 400f)
        )

        val horseRegionsPx = horseRegionsDp.mapValues { (_, rect) ->
            RectF(
                dpToPx(rect.left),
                dpToPx(rect.top),
                dpToPx(rect.right),
                dpToPx(rect.bottom)
            )
        }

        return horseRegionsPx.entries.find { (_, rect) -> rect.contains(x, y) }?.key
    }

    fun Context.dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun isCorrectToolForPart(toolName: String, horsePart: String?): Boolean {
        // Implement logic to check if the tool is correct for the horse part
        // This is a placeholder, you need to implement the actual logic
        return true
    }

    private fun playSuccessEffect() {
        // Play a success sound and visual effect
    }

    private fun playErrorEffect() {
        // Play an error sound and visual effect
    }

    private fun updateScore() {
        // Update the score
        score += 10 // Example: Add 10 points for each correct action
        binding.scoreValue.text = score.toString()
    }

    private fun incrementErrors() {
        // Increment the error count
        errors++
        binding.errorValue.text = errors.toString()
    }
}