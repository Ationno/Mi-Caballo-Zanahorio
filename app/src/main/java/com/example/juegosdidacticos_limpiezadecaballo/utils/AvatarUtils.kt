package com.example.juegosdidacticos_limpiezadecaballo.utils

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

}
