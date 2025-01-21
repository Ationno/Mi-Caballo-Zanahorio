package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TeraphistEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.UserInitPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils

class UserInitFragment : Fragment() {

    private var _binding: UserInitPageBinding? = null
    private val binding get() = _binding!!
    private var selectedUser: NamedEntity? = null

    //non graphical initializations here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedUser =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    it.getParcelable("selectedUser", NamedEntity::class.java) // >= API 33
                } else {
                    @Suppress("DEPRECATION")
                    it.getParcelable("selectedUser") // < API 33
                }
        }
    }

    //graphical initializations here
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

        selectedUser?.let { user ->
            binding.userName.text = user.name
            binding.userProfile.setImageResource(AvatarUtils.getAvatarResource(user.avatar))

            when (user) {
                is PacientEntity -> {
                    binding.playButton.visibility = View.VISIBLE
                    binding.informationButton.visibility = View.VISIBLE
                    binding.userDifficulty.visibility = View.VISIBLE
                    binding.userDifficulty.text = getString(R.string.dificultad_usuario,
                        context?.let { user.difficulty.getDisplayName(it) })

                }

                is TeraphistEntity -> {
                    binding.pacientsButton.visibility = View.VISIBLE
                    binding.registerButton.visibility = View.VISIBLE
                    binding.configurationButton.visibility= View.VISIBLE
                }
            }
        }
    }

    private fun Difficulty.getDisplayName(context: Context): String {
        return context.getString(this.displayNameResId)
    }
}