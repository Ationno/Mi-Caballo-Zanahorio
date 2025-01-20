package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.ui.adapter.NamedEntityAdapter
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarResource
import kotlinx.coroutines.launch

class UserSelectionFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonPacients: Button
    private lateinit var buttonTherapists: Button
    private lateinit var buttonStart: Button
    private lateinit var profileAvatar: ImageView
    private lateinit var userName: TextView
    private var selectedUser: NamedEntity? = null
    private var lastSelectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout
        return inflater.inflate(R.layout.user_selection_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        buttonPacients = view.findViewById(R.id.pacients)
        buttonTherapists = view.findViewById(R.id.therapist)
        buttonStart = view.findViewById(R.id.iniciar)
        profileAvatar = view.findViewById(R.id.profile_example)
        userName = view.findViewById(R.id.user_name)

        recyclerView.layoutManager = GridLayoutManager(context, 3)

        lastSelectedButton = buttonPacients
        buttonPacients.setBackgroundResource(R.drawable.button_pressed_background)

        userViewModel.allPacients.observe(viewLifecycleOwner, { pacients ->
            pacients?.let {
                recyclerView.adapter = NamedEntityAdapter(it) { user ->
                    selectedUser = user
                    profileAvatar.setImageResource(getAvatarResource(user.avatar))
                    userName.text = user.name
                }
            }
        })

        buttonPacients.setOnClickListener {
            selectButton(it as Button)
            userViewModel.allPacients.observe(viewLifecycleOwner, { pacients ->
                pacients?.let {
                    recyclerView.adapter = NamedEntityAdapter(it) { user ->
                        selectedUser = user
                        profileAvatar.setImageResource(getAvatarResource(user.avatar))
                        userName.text = user.name
                    }
                }
            })
        }

        buttonTherapists.setOnClickListener {
            selectButton(it as Button)
            userViewModel.allTeraphists.observe(viewLifecycleOwner, { therapists ->
                therapists?.let {
                    recyclerView.adapter = NamedEntityAdapter(it) { user ->
                        selectedUser = user
                        profileAvatar.setImageResource(getAvatarResource(user.avatar))
                        userName.text = user.name
                    }
                }
            })
        }

        buttonStart.setOnClickListener {
            selectedUser?.let {
                // Acción cuando hay un usuario seleccionado
                findNavController().navigate(R.id.action_userSelectionPage_to_userInitPage)
            } ?: run {
                Toast.makeText(context, "Selecciona un usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // Insertar datos de ejemplo
        lifecycleScope.launch {
            userViewModel.insertExamplePacients()
            userViewModel.insertExampleTeraphists()
        }
    }

    // Función para manejar la selección de los botones
    private fun selectButton(selectedButton: Button) {
        lastSelectedButton?.setBackgroundResource(R.drawable.button_background)
        selectedButton.setBackgroundResource(R.drawable.button_pressed_background)
        lastSelectedButton = selectedButton
    }
}
