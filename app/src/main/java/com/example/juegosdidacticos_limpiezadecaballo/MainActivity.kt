package com.example.juegosdidacticos_limpiezadecaballo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.MainPageBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}