package com.example.juegosdidacticos_limpiezadecaballo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.MainPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.fragment.UserInitFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainPageBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (intent.getBooleanExtra("SHOW_USER_INIT_FRAGMENT", false)) {

            val selectedUser = intent.getParcelableExtra("USER_DATA", NamedEntity::class.java)
            val args = Bundle().apply {
                putParcelable("selectedUser", selectedUser)
            }
            navController.navigate(R.id.UserInitPage, args)
        }
    }
}