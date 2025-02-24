package com.example.juegosdidacticos_limpiezadecaballo.ui.fragment

import UserHistoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.databinding.UserHistoryPageBinding
import com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel.GameViewModel
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils

class UserHistoryFragment : Fragment() {

    private var _binding: UserHistoryPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GameViewModel
    private lateinit var adapter: UserHistoryAdapter
    private var currentPage = 1
    private val itemsPerPage = 10
    private var selectedUser: PatientEntity? = null
    private var cachedGameHistory: List<GameStateEntity> = emptyList()
    private val pageButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedUser = it.getParcelable("selectedUser", PatientEntity::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserHistoryPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        adapter = UserHistoryAdapter()
        binding.tableRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tableRecyclerView.adapter = adapter

        selectedUser?.let {
            viewModel.getGameStatesOfUser(it.id).observe(viewLifecycleOwner) { history ->
                if (history != null) {
                    cachedGameHistory = history
                    updatePaginationButtons(history.size)
                    navigateToPage(currentPage)
                }
            }
            updateUI(it)
        }

        binding.prevButton.setOnClickListener { navigatePage(-1) }
        binding.nextButton.setOnClickListener { navigatePage(1) }
    }

    private fun updatePaginationButtons(totalItems: Int) {
        val totalPages = (totalItems + itemsPerPage - 1) / itemsPerPage

        binding.navigationButtons.removeAllViews()
        pageButtons.clear()

        binding.navigationButtons.addView(binding.prevButton)

        for (i in 1..totalPages) {
            val pageButton = createPageButton(i)
            binding.navigationButtons.addView(pageButton)
            pageButtons.add(pageButton)
        }

        binding.navigationButtons.addView(binding.nextButton)

        binding.prevButton.isEnabled = currentPage > 1
        binding.nextButton.isEnabled = currentPage < totalPages

        updateButtonColors()
    }

    private fun createPageButton(pageNumber: Int): Button {
        return Button(requireContext()).apply {
            text = pageNumber.toString()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 2.dpToPx()
            }
            background = null
            setTextColor(resources.getColor(R.color.white, null))
            textSize = 36f
            setOnClickListener { navigateToPage(pageNumber) }
        }
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun navigatePage(direction: Int) {
        val newPage = currentPage + direction
        if (newPage > 0) {
            navigateToPage(newPage)
        }
    }

    private fun navigateToPage(page: Int) {
        currentPage = page

        val sortedList = cachedGameHistory.sortedByDescending { it.date }

        val startIndex = (page - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, sortedList.size)
        val paginatedList = sortedList.subList(startIndex, endIndex)

        adapter.submitList(paginatedList)

        updatePaginationButtons(cachedGameHistory.size)
        updateButtonColors()
    }

    private fun updateButtonColors() {
        for ((index, button) in pageButtons.withIndex()) {
            val pageNumber = index + 1
            if (pageNumber == currentPage) {
                button.setTextColor(resources.getColor(R.color.bright_orange, null))
            } else {
                button.setTextColor(resources.getColor(R.color.white, null))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(user: NamedEntity) {
        binding.userName.text = user.name
        binding.userProfile.setImageResource(AvatarUtils.getAvatarResource(user.avatar))
    }
}