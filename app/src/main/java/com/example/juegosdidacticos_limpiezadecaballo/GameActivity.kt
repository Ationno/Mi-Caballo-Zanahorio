package com.example.juegosdidacticos_limpiezadecaballo

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.databinding.GamePageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var binding: GamePageBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = GamePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pacientId = arguments?.let { bundle ->
            GameActivityArgs.fromBundle(bundle).pacientId
        }

        pacientId?.let { id: Int ->
            lifecycleScope.launch {
                val config = userViewModel.getConfigByPacientId(id)
                config?.let {
                    initializeDifficulty(it.difficulty)
                }
            }
        }
    }

    private fun initializeDifficulty(difficulty: Difficulty) {
        // Set the difficulty in the game logic or UI
        when (difficulty) {
            Difficulty.EASY -> {
                // Set easy difficulty
            }
            Difficulty.MEDIUM -> {
                // Set medium difficulty

            }
            Difficulty.HARD -> {
                // Set hard difficulty
            }
        }
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View {
        return binding.root
    }
}