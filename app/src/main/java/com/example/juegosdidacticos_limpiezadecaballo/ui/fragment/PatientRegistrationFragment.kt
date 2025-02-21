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
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Genre
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.PatientRegistrationPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarType
import com.example.juegosdidacticos_limpiezadecaballo.utils.capitalizeFirstLetter
import kotlinx.coroutines.launch

class PatientRegistrationFragment : Fragment() {

    private var _binding: PatientRegistrationPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedAvatarId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PatientRegistrationPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAvatarSelection()

        binding.start.setOnClickListener {
            registerPatient()
        }

        binding.cancel.setOnClickListener {
            navigateBack()
        }
    }

    private fun setupAvatarSelection() {
        val avatars = listOf(
            binding.commonPatientFields.commonFields.avatar1,
            binding.commonPatientFields.commonFields.avatar2,
            binding.commonPatientFields.commonFields.avatar3,
            binding.commonPatientFields.commonFields.avatar4,
            binding.commonPatientFields.commonFields.avatar5
        )

        AvatarUtils.setupAvatarSelection(avatars) { selectedAvatarId ->
            this.selectedAvatarId = selectedAvatarId
        }
    }

    private fun registerPatient() {
        val name = binding.commonPatientFields.commonFields.name.text.toString().trim().capitalizeFirstLetter()
        val surname = binding.commonPatientFields.commonFields.surname.text.toString().trim().capitalizeFirstLetter()
        val age = binding.commonPatientFields.age.text.toString().trim().toIntOrNull()
        val observations = binding.commonPatientFields.observations.text.toString().trim().capitalizeFirstLetter()
        val genre = when (binding.commonPatientFields.patientGenre.checkedRadioButtonId) {
            R.id.male -> Genre.MALE
            R.id.female -> Genre.FEMALE
            else -> null
        }

        if (name.isEmpty() || surname.isEmpty() || age == null || genre == null || selectedAvatarId == null) {
            Toast.makeText(
                requireContext(),
                "Por favor, complete todos los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val patient = PatientEntity(
            name = name,
            surname = surname,
            age = age,
            observations = observations,
            genre = genre,
            avatar = getAvatarType(selectedAvatarId!!)
        )

        lifecycleScope.launch {
            try {
                userViewModel.insertPatient(patient)
                Toast.makeText(
                    requireContext(),
                    "Paciente registrado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                navigateBack()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al registrar el paciente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateBack() {
        findNavController().navigate(R.id.action_PatientRegistrationPage_to_UserManagementPage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}