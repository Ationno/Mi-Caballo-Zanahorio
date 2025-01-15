package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.UserViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.juegosdidacticos_limpiezadecaballo.ui.adapter.UserListAdapter

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

        // Observar los cambios en la lista de usuarios desde el ViewModel
        userViewModel.allUsers.observe(viewLifecycleOwner, Observer { users ->
            users?.let {
                recyclerViewAdapter = UserListAdapter(it)
                recyclerView.adapter = recyclerViewAdapter
            }
        })

        userViewModel.insertExampleUsers()

        return binding
    }
}
