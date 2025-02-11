package com.example.juegosdidacticos_limpiezadecaballo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity
import com.example.juegosdidacticos_limpiezadecaballo.utils.AvatarUtils.getAvatarResource

class NamedEntityAdapter(
    private val items: List<NamedEntity>,
    private val onItemSelected: (NamedEntity) -> Unit
) : RecyclerView.Adapter<NamedEntityAdapter.UserViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        if (items.isNotEmpty()) {
            selectedPosition = 0
            onItemSelected(items[0])
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textName)
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = items[position]
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

    override fun getItemCount(): Int = items.size

}
