package com.example.juegosdidacticos_limpiezadecaballo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarResource

class UserManagementAdapter(
    private val items: List<NamedEntity>,
    private val onItemSelected: (NamedEntity) -> Unit,
    private val onModifyUser: (NamedEntity) -> Unit,
    private val onAddUser: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    companion object {
        private const val TYPE_ADD_USER = 0
        private const val TYPE_USER = 1
    }

    init {
        if (items.isNotEmpty()) {
            selectedPosition = 1 // Because the first item is the "Add User" button
            onItemSelected(items[0])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD_USER else TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ADD_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_user, parent, false)
                AddUserViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_item, parent, false)
                UserViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddUserViewHolder -> {
                holder.addUserImage.setOnClickListener {
                    onAddUser()
                }
            }

            is UserViewHolder -> {
                val item = items[position - 1] // Adjust position for the "Add User" button
                holder.nameTextView.text = item.name
                holder.avatarImageView.setImageResource(getAvatarResource(item.avatar))

                holder.itemView.isSelected = position == selectedPosition

                if (position == selectedPosition) {
                    holder.avatarImageView.setBackgroundResource(R.drawable.avatar_selected_border)
                    holder.nameTextView.setTextColor(holder.itemView.context.getColor(R.color.dark_orange))
                } else {
                    holder.avatarImageView.setBackgroundResource(0)
                    holder.nameTextView.setTextColor(holder.itemView.context.getColor(R.color.light_font))
                }
                holder.itemView.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = holder.adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    onItemSelected(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size + 1 // +1 for the "Add User" button

    class AddUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addUserImage: ImageView = itemView.findViewById(R.id.addUserImage)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textName)
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatar)

    }
}