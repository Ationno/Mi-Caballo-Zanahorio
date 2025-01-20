package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.R

class UserInitFragment : Fragment() {

    private lateinit var buttonChangeProfile: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_init_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonChangeProfile = view.findViewById(R.id.change_profile_button)

        buttonChangeProfile.setOnClickListener {
            findNavController().navigate(R.id.action_userInitPage_to_userSelectionPage)
        }

    }
}
