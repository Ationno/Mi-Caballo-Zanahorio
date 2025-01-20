package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.ui.adapter.NamedEntityAdapter
import kotlinx.coroutines.launch

class UserSelectionFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.user_selection_page, container, false)

        recyclerView = binding.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3) // 3 columnas


        val buttonPacients = binding.findViewById<Button>(R.id.pacients)
        val buttonTherapists = binding.findViewById<Button>(R.id.therapist)

        var lastSelectedButton: Button? = buttonPacients

        buttonPacients.setBackgroundResource(R.drawable.button_pressed_background)
        userViewModel.allPacients.observe(viewLifecycleOwner, Observer { pacients ->
            pacients?.let {
                recyclerViewAdapter = NamedEntityAdapter(it)
                recyclerView.adapter = recyclerViewAdapter
            }
        })

        buttonPacients.setOnClickListener {
            lastSelectedButton?.setBackgroundResource(R.drawable.button_background)
            it.setBackgroundResource(R.drawable.button_pressed_background)
            lastSelectedButton = it as Button
            userViewModel.allPacients.observe(viewLifecycleOwner, Observer { pacients ->
                pacients?.let {
                    recyclerViewAdapter = NamedEntityAdapter(it)
                    recyclerView.adapter = recyclerViewAdapter
                }
            })
        }

        buttonTherapists.setOnClickListener {
            lastSelectedButton?.setBackgroundResource(R.drawable.button_background)
            it.setBackgroundResource(R.drawable.button_pressed_background)
            lastSelectedButton = it as Button
            userViewModel.allTeraphists.observe(viewLifecycleOwner, Observer { teraphists ->
                teraphists?.let {
                    recyclerViewAdapter = NamedEntityAdapter(it)
                    recyclerView.adapter = recyclerViewAdapter
                }
            })
        }

        lifecycleScope.launch {
            userViewModel.insertExamplePacients()
            userViewModel.insertExampleTeraphists()
        }


        return binding
    }
}
