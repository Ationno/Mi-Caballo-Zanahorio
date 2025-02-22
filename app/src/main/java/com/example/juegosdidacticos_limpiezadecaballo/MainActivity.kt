package com.example.juegosdidacticos_limpiezadecaballo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.MainPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.utils.BackgroundMusicPlayer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainPageBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.backButton.setOnClickListener {
            if (navController.currentDestination?.id == R.id.UserInitPage) {
                navController.navigate(R.id.UserSelectionPage)
            } else {
                navController.navigateUp()
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.BeginPageFragment, R.id.UserSelectionPage -> {
                    binding.backButton.visibility = View.GONE
                }
                R.id.TherapistRegistrationPage, R.id.TherapistConfigPage,
                R.id.PatientRegistrationPage, R.id.PatientConfigPage -> {
                    binding.backButton.visibility = View.VISIBLE
                    moveBackButtonToRight()
                }
                else -> {
                    binding.backButton.visibility = View.VISIBLE
                    resetBackButtonPosition()
                }
            }
        }

        if (intent.getBooleanExtra("SHOW_USER_INIT_FRAGMENT", false)) {
            val selectedUser = intent.getParcelableExtra("USER_DATA", NamedEntity::class.java)
            val args = Bundle().apply {
                putParcelable("selectedUser", selectedUser)
            }
            navController.navigate(R.id.UserInitPage, args)
        }

        BackgroundMusicPlayer.start(this, R.raw.menu_music)
    }

    private fun moveBackButtonToRight() {
        val layoutParams = binding.backButton.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.END or Gravity.TOP
        binding.backButton.layoutParams = layoutParams

        binding.backButton.imageTintList = ContextCompat.getColorStateList(this, R.color.background_color)
    }

    private fun resetBackButtonPosition() {
        val layoutParams = binding.backButton.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.START or Gravity.TOP
        binding.backButton.layoutParams = layoutParams


        binding.backButton.imageTintList = ContextCompat.getColorStateList(this, R.color.dark_orange)
    }

    override fun onRestart() {
        super.onRestart()
        BackgroundMusicPlayer.changeMusic(applicationContext, R.raw.menu_music)
    }

    override fun onResume() {
        super.onResume()
        BackgroundMusicPlayer.changeMusic(applicationContext, R.raw.menu_music)
    }
}