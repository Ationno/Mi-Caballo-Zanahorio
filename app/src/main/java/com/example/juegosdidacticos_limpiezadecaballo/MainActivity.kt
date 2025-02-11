package com.example.juegosdidacticos_limpiezadecaballo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        if (intent.getBooleanExtra("SHOW_USER_INIT_FRAGMENT", false)) {

            val selectedUser = intent.getParcelableExtra("USER_DATA", NamedEntity::class.java)
            val args = Bundle().apply {
                putParcelable("selectedUser", selectedUser)
            }
            navController.navigate(R.id.UserInitPage, args)
        }

        BackgroundMusicPlayer.start(this, R.raw.menu_music)
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