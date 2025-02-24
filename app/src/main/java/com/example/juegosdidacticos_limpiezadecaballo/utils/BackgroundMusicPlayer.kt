package com.example.juegosdidacticos_limpiezadecaballo.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

object BackgroundMusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var volumeFinal: Float = 0f

    fun start(context: Context, musicResId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, musicResId)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            setVolume(50, 50)
        }
    }

    fun changeMusic(context: Context, newMusicResId: Int) {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }

        mediaPlayer = MediaPlayer.create(context.applicationContext, newMusicResId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(volumeFinal / 100f, volumeFinal / 100f)
        mediaPlayer?.start()
    }

    fun setVolume(volumeMusic: Int, volumeTotal: Int) {
        volumeFinal = volumeTotal / 100f * volumeMusic
        mediaPlayer?.setVolume(volumeFinal / 100f, volumeFinal / 100f)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}