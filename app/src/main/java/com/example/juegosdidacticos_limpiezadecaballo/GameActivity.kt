package com.example.juegosdidacticos_limpiezadecaballo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.GamePageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var binding: GamePageBinding
    private val userViewModel: UserViewModel by viewModels()
    private var user: PacientEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = GamePageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        user = intent.getParcelableExtra("user", PacientEntity::class.java)

        val pacientId = user?.id

        pacientId?.let { id: Int ->
            lifecycleScope.launch {
                val config = userViewModel.getConfigByPacientId(id)
                config?.let {
                    initializeDifficulty(it.difficulty)
                }
            }
        }

        setupClickListeners()
    }

    private fun initializeDifficulty(difficulty: Difficulty) {
        when (difficulty) {
            Difficulty.EASY -> {
            }
            Difficulty.MEDIUM -> {
            }
            Difficulty.HARD -> {
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackButtonClicked()
        }

        binding.btnSettings.setOnClickListener {
            onSettingsButtonClicked()
        }

        binding.btnPause.setOnClickListener {
            onPauseButtonClicked()
        }

        binding.btnHint.setOnClickListener {
            onHintButtonClicked()
        }
    }

    private fun onBackButtonClicked() {
        val dialogView = layoutInflater.inflate(R.layout.confirmation_page, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.yesButton).setOnClickListener {
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

        dialogView.findViewById<View>(R.id.noButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun onSettingsButtonClicked() {
    }

    private fun onPauseButtonClicked() {
    }

    private fun onHintButtonClicked() {
    }
}