package com.example.juegosdidacticos_limpiezadecaballo.utils

import android.view.View
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.R

object AvatarUtils {
    fun getAvatarResource(avatarType: Avatar): Int {
        return when (avatarType) {
            Avatar.FIRST -> R.drawable.first_avatar
            Avatar.SECOND -> R.drawable.second_avatar
            Avatar.THIRD -> R.drawable.third_avatar
            Avatar.FOURTH -> R.drawable.fourth_avatar
            Avatar.FIFTH -> R.drawable.fifth_avatar
        }
    }

    fun getAvatarType(resourceId: Int): Avatar {
        return when (resourceId) {
            R.drawable.first_avatar -> Avatar.FIRST
            R.drawable.second_avatar -> Avatar.SECOND
            R.drawable.third_avatar -> Avatar.THIRD
            R.drawable.fourth_avatar -> Avatar.FOURTH
            R.drawable.fifth_avatar -> Avatar.FIFTH
            else -> throw IllegalArgumentException("Invalid resource ID")
        }
    }

    fun setupAvatarSelection(
        avatars: List<View>,
        onAvatarSelected: (avatarId: Int?) -> Unit
    ) {
        avatars.forEach { avatar ->
            avatar.setOnClickListener {
                val selectedAvatarId = when (avatar.id) {
                    R.id.avatar1 -> R.drawable.first_avatar
                    R.id.avatar2 -> R.drawable.second_avatar
                    R.id.avatar3 -> R.drawable.third_avatar
                    R.id.avatar4 -> R.drawable.fourth_avatar
                    R.id.avatar5 -> R.drawable.fifth_avatar
                    else -> null
                }

                avatars.forEach { it.setBackgroundResource(0) }
                avatar.setBackgroundResource(R.drawable.avatar_selected_border)

                onAvatarSelected(selectedAvatarId)
            }
        }
    }
}
