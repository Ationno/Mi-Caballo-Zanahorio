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

        setupAnimations()

        view.setOnClickListener {
            findNavController().navigate(R.id.action_beginPage_to_userSelectionPage)
        }

        return view
    }

    private fun setupAnimations() {
        val cedicaLogo = binding.cedicaLogo
        val horse = binding.horse

        val scaleDownCedicaX = ObjectAnimator.ofFloat(cedicaLogo, "scaleX", 0.9f)
        val scaleDownCedicaY = ObjectAnimator.ofFloat(cedicaLogo, "scaleY", 0.9f)
        val scaleUpCedicaX = ObjectAnimator.ofFloat(cedicaLogo, "scaleX", 1f)
        val scaleUpCedicaY = ObjectAnimator.ofFloat(cedicaLogo, "scaleY", 1f)

        val scaleDownHorseX = ObjectAnimator.ofFloat(horse, "scaleX", 0.9f)
        val scaleDownHorseY = ObjectAnimator.ofFloat(horse, "scaleY", 0.9f)
        val scaleUpHorseX = ObjectAnimator.ofFloat(horse, "scaleX", 1f)
        val scaleUpHorseY = ObjectAnimator.ofFloat(horse, "scaleY", 1f)

        val duration = 1500L
        scaleDownCedicaX.duration = duration
        scaleDownCedicaY.duration = duration
        scaleUpCedicaX.duration = duration
        scaleUpCedicaY.duration = duration

        scaleDownHorseX.duration = duration
        scaleDownHorseY.duration = duration
        scaleUpHorseX.duration = duration
        scaleUpHorseY.duration = duration

        val cedicaAnimatorSet = AnimatorSet()
        cedicaAnimatorSet.play(scaleDownCedicaX).with(scaleDownCedicaY)
        cedicaAnimatorSet.play(scaleUpCedicaX).after(scaleDownCedicaX)
        cedicaAnimatorSet.play(scaleUpCedicaY).after(scaleDownCedicaY)

        val horseAnimatorSet = AnimatorSet()
        horseAnimatorSet.play(scaleDownHorseX).with(scaleDownHorseY)
        horseAnimatorSet.play(scaleUpHorseX).after(scaleDownHorseX)
        horseAnimatorSet.play(scaleUpHorseY).after(scaleDownHorseY)

        cedicaAnimatorSet.start()
        horseAnimatorSet.start()

        cedicaAnimatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                cedicaAnimatorSet.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })

        horseAnimatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                horseAnimatorSet.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
