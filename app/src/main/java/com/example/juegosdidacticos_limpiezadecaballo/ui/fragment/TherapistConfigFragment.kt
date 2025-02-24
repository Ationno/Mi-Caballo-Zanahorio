package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TherapistEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.TherapistConfigPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarType
import com.example.juegosdidacticos_limpiezadecaballo.utils.capitalizeFirstLetter
import kotlinx.coroutines.launch

class TherapistConfigFragment : Fragment() {

    private var _binding: TherapistConfigPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedUser: TherapistEntity? = null
    private var selectedAvatarId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedUser = it.getParcelable("selectedUser", TherapistEntity::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TherapistConfigPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedUser?.let { user ->
            populateUserData(user)
        }

        setupAvatarSelection()

        binding.modify.setOnClickListener {
            modifyTherapist()
        }

        binding.delete.setOnClickListener {
            deleteTherapist()
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

        AvatarUtils.setupAvatarSelection(avatars) { selectedAvatarId ->
            this.selectedAvatarId = selectedAvatarId
        }
    }

    private fun modifyTherapist() {
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
            id = selectedUser!!.id,
            name = name,
            surname = surname,
            avatar = getAvatarType(selectedAvatarId!!)
        )

        lifecycleScope.launch {
            try {
                userViewModel.updateTherapist(therapist)
                Toast.makeText(
                    requireContext(),
                    "Terapeuta modificado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                selectedUser = therapist
                navigateBack()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al modificar el terapeuta",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun populateUserData(user: TherapistEntity) {
        binding.commonFields.name.setText(user.name)
        binding.commonFields.surname.setText(user.surname)

        this.selectedAvatarId = AvatarUtils.getAvatarResource(user.avatar)

        when (user.avatar) {
            Avatar.FIRST -> binding.commonFields.avatar1.setBackgroundResource(R.drawable.avatar_selected_border)
            Avatar.SECOND -> binding.commonFields.avatar2.setBackgroundResource(
                R.drawable.avatar_selected_border
            )

            Avatar.THIRD -> binding.commonFields.avatar3.setBackgroundResource(R.drawable.avatar_selected_border)
            Avatar.FOURTH -> binding.commonFields.avatar4.setBackgroundResource(
                R.drawable.avatar_selected_border
            )

            Avatar.FIFTH -> binding.commonFields.avatar5.setBackgroundResource(R.drawable.avatar_selected_border)

        }

    }

    private fun deleteTherapist() {
        val dialogView = layoutInflater.inflate(R.layout.confirm_action, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val userName = selectedUser?.name

        val infoTitle = dialogView.findViewById<TextView>(R.id.infoTitle)
        infoTitle.text = "Eliminar a $userName"

        val infoText = dialogView.findViewById<TextView>(R.id.infoText)
        infoText.text = "¿Estas seguro que quieres eliminar el perfil de $userName? Se borraran todos sus datos de forma permanente."


        dialogView.findViewById<View>(R.id.confirmButton).setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                try {
                    userViewModel.deleteTherapist(selectedUser!!)
                    Toast.makeText(
                        requireContext(),
                        "Terapeuta eliminado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToSelection()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar el terapeuta",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

    }


    private fun navigateBack() {
        findNavController().navigate(
            R.id.action_TherapistConfigPage_to_UserInitPage,
            Bundle().apply {
                putParcelable("selectedUser", selectedUser)
            }
        )
    }

    private fun navigateToSelection() {
        findNavController().navigate(R.id.action_TherapistConfigPage_to_UserSelectionPage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}