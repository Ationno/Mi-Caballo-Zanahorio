package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.GameActivity
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TherapistEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.UserInitPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils
import kotlinx.coroutines.launch

class UserInitFragment : Fragment() {

    private var _binding: UserInitPageBinding? = null
    private val binding get() = _binding!!
    private var selectedUser: NamedEntity? = null
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedUser = it.getParcelable("selectedUser", NamedEntity::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserInitPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.changeProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_userInitPage_to_userSelectionPage)
        }

        binding.aboutUsButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.about_us, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialogView.findViewById<View>(R.id.closeDialogButton).setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }

        selectedUser?.let { user ->
            binding.userName.text = user.name
            binding.userProfile.setImageResource(AvatarUtils.getAvatarResource(user.avatar))

            when (user) {
                is PatientEntity -> {
                    binding.playButton.visibility = View.VISIBLE
                    binding.informationButton.visibility = View.VISIBLE
                    binding.userDifficulty.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        val config = userViewModel.getConfigByPatientId(user.id)
                        binding.userDifficulty.text = getString(
                            R.string.user_difficulty,
                            config?.difficulty?.getDisplayDifficulty()
                        )
                    }
                }

                is TherapistEntity -> {
                    binding.managementButton.visibility = View.VISIBLE
                    binding.myProfile.visibility = View.VISIBLE
                    binding.histories.visibility = View.VISIBLE
                }

                else -> {}
            }
        }

        binding.managementButton.setOnClickListener {
            findNavController().navigate(R.id.action_userInitPage_to_UserManagementPage)
        }

        binding.playButton.setOnClickListener {
            val intent = Intent(requireContext(), GameActivity::class.java)
            intent.putExtra("USER", selectedUser)
            startActivity(intent)
        }
    }
}