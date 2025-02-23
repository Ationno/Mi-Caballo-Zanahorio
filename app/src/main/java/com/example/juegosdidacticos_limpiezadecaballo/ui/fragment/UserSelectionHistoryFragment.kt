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
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.UserSelectionHistoryPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.databinding.UserSelectionPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.adapter.NamedEntityAdapter
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarResource
import kotlinx.coroutines.launch

class UserSelectionHistoryFragment : Fragment() {


    private var _binding: UserSelectionHistoryPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var selectedUser: PatientEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserSelectionHistoryPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        val profileAvatar = binding.profileExample
        val userName = binding.userName

        recyclerView.layoutManager = GridLayoutManager(context, 3)

        userViewModel.allPatients.observe(viewLifecycleOwner, { patients ->
            patients?.let {
                recyclerView.adapter = NamedEntityAdapter(it) { user ->
                    selectedUser = user as PatientEntity
                    profileAvatar.setImageResource(getAvatarResource(user.avatar))
                    userName.text = user.name
                }
            }
        })

        binding.historyButton.setOnClickListener {
            selectedUser?.let { user ->
                val action =
                    UserSelectionHistoryFragmentDirections.actionUserSelectionHistoryPageToUserHistoryPage(user)
                findNavController().navigate(action)
            } ?: run {
                Toast.makeText(context, "Selecciona un usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
