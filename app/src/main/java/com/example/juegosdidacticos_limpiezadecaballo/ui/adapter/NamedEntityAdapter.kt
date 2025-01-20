package com.example.juegosdidacticos_limpiezadecaballo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.data.model.NamedEntity

class NamedEntityAdapter(private val items: List<NamedEntity>) : RecyclerView.Adapter<NamedEntityAdapter.UserViewHolder>() {

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
    }

    override fun getItemCount(): Int = items.size

    private fun getAvatarResource(avatarType: Avatar): Int {
        return when (avatarType) {
            Avatar.FIRST -> R.drawable.first_avatar
            Avatar.SECOND -> R.drawable.second_avatar
            Avatar.THIRD -> R.drawable.third_avatar
            Avatar.FOURTH -> R.drawable.fourth_avatar
            Avatar.FIFTH -> R.drawable.fifth_avatar
        }
    }
}
