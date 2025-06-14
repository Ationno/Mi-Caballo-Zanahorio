package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.UserSelectionPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.adapter.NamedEntityAdapter
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarResource
import com.example.juegosdidacticos_limpiezadecaballo.utils.BackgroundMusicPlayer
import kotlinx.coroutines.launch

class UserSelectionFragment : Fragment() {


    private var _binding: UserSelectionPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedUser: NamedEntity? = null
    private var lastSelectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserSelectionPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BackgroundMusicPlayer.setVolume(50, 50)
        val recyclerView = binding.recyclerView
        val buttonPatients = binding.patients
        val profileAvatar = binding.profileExample
        val userName = binding.userName

        recyclerView.layoutManager = GridLayoutManager(context, 3)

        lastSelectedButton = buttonPatients
        buttonPatients.setBackgroundResource(R.drawable.button_pressed_background)

        userViewModel.allPatients.observe(viewLifecycleOwner, { patients ->
            patients?.let {
                recyclerView.adapter = NamedEntityAdapter(it) { user ->
                    selectedUser = user
                    profileAvatar.setImageResource(getAvatarResource(user.avatar))
                    userName.text = user.name
                }
            }
        })

        buttonPatients.setOnClickListener {
            selectButton(it as Button)
            userViewModel.allPatients.observe(viewLifecycleOwner, { patients ->
                patients?.let {
                    recyclerView.adapter = NamedEntityAdapter(it) { user ->
                        selectedUser = user
                        profileAvatar.setImageResource(getAvatarResource(user.avatar))
                        userName.text = user.name
                    }
                }
            })
        }

        binding.therapist.setOnClickListener {
            selectButton(it as Button)
            userViewModel.allTherapists.observe(viewLifecycleOwner, { therapists ->
                therapists?.let {
                    recyclerView.adapter = NamedEntityAdapter(it) { user ->
                        selectedUser = user
                        profileAvatar.setImageResource(getAvatarResource(user.avatar))
                        userName.text = user.name
                    }
                }
            })
        }

        binding.start.setOnClickListener {
            selectedUser?.let { user ->
                val action =
                    UserSelectionFragmentDirections.actionUserSelectionPageToUserInitPage(user)
                findNavController().navigate(action)
            } ?: run {
                Toast.makeText(context, "Selecciona un usuario", Toast.LENGTH_SHORT).show()
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
}
