package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.UserManagementPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.adapter.UserManagementAdapter
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarResource
import kotlinx.coroutines.launch

class UserManagementFragment : Fragment() {

    private var _binding: UserManagementPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedUser: NamedEntity? = null
    private var lastSelectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserManagementPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        val buttonPatients = binding.patients
        val profileAvatar = binding.profileExample
        val userName = binding.userName
        val modifyIcon = binding.modifyIcon

        recyclerView.layoutManager = GridLayoutManager(context, 3)

        lastSelectedButton = buttonPatients
        buttonPatients.setBackgroundResource(R.drawable.button_pressed_background)

        observeUsers(userViewModel.allPatients, recyclerView, profileAvatar, userName, modifyIcon)

        buttonPatients.setOnClickListener {
            selectButton(it as Button)
            observeUsers(userViewModel.allPatients, recyclerView, profileAvatar, userName, modifyIcon)
        }

        binding.therapist.setOnClickListener {
            selectButton(it as Button)
            observeUsers(userViewModel.allTherapists, recyclerView, profileAvatar, userName, modifyIcon)
        }

        modifyIcon.setOnClickListener {
            selectedUser?.let { user ->
                if (user is PatientEntity) {
                    // Handle modify patient action
                    // You can open a dialog or another fragment to modify the patient
                    showModifyPatientDialog(user)
                }
            }
        }

        lifecycleScope.launch {
            userViewModel.insertExamplePatients()
            userViewModel.insertExampleTherapists()
        }
    }

    private fun selectButton(selectedButton: Button) {
        lastSelectedButton?.setBackgroundResource(R.drawable.button_background)
        selectedButton.setBackgroundResource(R.drawable.button_pressed_background)
        lastSelectedButton = selectedButton
    }

    private fun <T : NamedEntity> observeUsers(
        userList: LiveData<List<T>>,
        recyclerView: RecyclerView,
        profileAvatar: ImageView,
        userName: TextView,
        modifyIcon: ImageView // Add this parameter
    ) {
        userList.observe(viewLifecycleOwner, { users ->
            users?.let {
                recyclerView.adapter = UserManagementAdapter(
                    it,
                    { user ->
                        selectedUser = user
                        profileAvatar.setImageResource(getAvatarResource(user.avatar))
                        userName.text = user.name

                        if (user is PatientEntity) {
                            modifyIcon.visibility = View.VISIBLE
                        } else {
                            modifyIcon.visibility = View.GONE
                        }
                    },
                    { user ->
                        // Handle modify user action
                        // You can open a dialog or another fragment to modify the user
                    },
                    {
                        // Handle add user action
                        // You can open a dialog or another fragment to add a new user
                    }
                )
            }
        })
    }

    private fun showModifyPatientDialog(patient: PatientEntity) {
        // Implement your logic to show a dialog or fragment for modifying the patient
        // Example:
        // val dialog = ModifyPatientDialog(patient)
        // dialog.show(parentFragmentManager, "ModifyPatientDialog")
    }
}