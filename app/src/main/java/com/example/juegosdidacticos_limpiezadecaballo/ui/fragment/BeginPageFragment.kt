package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.juegosdidacticos_limpiezadecaballo.R

class BeginPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.begin_page, container, false)

        // Agregar un OnClickListener al view raíz para detectar toques
        view.setOnClickListener {
            // Navegar a la página de selección de usuario
            findNavController().navigate(R.id.action_beginPage_to_userSelectionPage)
        }

        return view
    }
}
