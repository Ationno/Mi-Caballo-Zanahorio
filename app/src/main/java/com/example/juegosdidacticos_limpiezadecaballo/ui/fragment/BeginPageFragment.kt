package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.animation.ObjectAnimator
import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.databinding.BeginPageBinding

class BeginPageFragment : Fragment() {

    private var _binding: BeginPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BeginPageBinding.inflate(inflater, container, false)
        val view = binding.root

        view.setOnClickListener {
            findNavController().navigate(R.id.action_beginPage_to_userSelectionPage)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
