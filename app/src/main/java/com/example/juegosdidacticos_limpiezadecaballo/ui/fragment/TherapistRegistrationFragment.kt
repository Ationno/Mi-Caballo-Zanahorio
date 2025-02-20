package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TherapistEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.TherapistRegistrationPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarType
import com.example.juegosdidacticos_limpiezadecaballo.utils.capitalizeFirstLetter
import kotlinx.coroutines.launch

class TherapistRegistrationFragment : Fragment() {

    private var _binding: TherapistRegistrationPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedAvatarId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TherapistRegistrationPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAvatarSelection()

        binding.start.setOnClickListener {
            registerTherapist()
        }
    }

    private fun setupAvatarSelection() {
        val avatars = listOf(
            binding.commonFields.avatar1,
            binding.commonFields.avatar2,
            binding.commonFields.avatar3,
            binding.commonFields.avatar4,
            binding.commonFields.avatar5
        )

        avatars.forEach { avatar ->
            avatar.setOnClickListener {
                selectedAvatarId = when (avatar.id) {
                    R.id.avatar1 -> R.drawable.first_avatar
                    R.id.avatar2 -> R.drawable.second_avatar
                    R.id.avatar3 -> R.drawable.third_avatar
                    R.id.avatar4 -> R.drawable.fourth_avatar
                    R.id.avatar5 -> R.drawable.fifth_avatar
                    else -> null
                }

                avatars.forEach { it.setBackgroundResource(0) }
                avatar.setBackgroundResource(R.drawable.avatar_selected_border)
            }
        }
    }

    private fun registerTherapist() {
        val name = binding.commonFields.name.text.toString().trim().capitalizeFirstLetter()
        val surname = binding.commonFields.surname.text.toString().trim().capitalizeFirstLetter()

        if (name.isEmpty() || surname.isEmpty() || selectedAvatarId == null) {
            Toast.makeText(
                requireContext(),
                "Por favor, complete todos los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val therapist = TherapistEntity(
            name = name,
            surname = surname,
            avatar = getAvatarType(selectedAvatarId!!)
        )


        lifecycleScope.launch {
            try {
                userViewModel.insertTherapist(therapist)
                Toast.makeText(
                    requireContext(),
                    "Terapeuta registrado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                navigateBack()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al registrar el terapeuta",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateBack() {
        findNavController().navigate(R.id.action_TherapistRegistrationPage_to_UserManagementPage)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}