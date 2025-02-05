package com.example.juegosdidacticos_limpiezadecaballo.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

object BackgroundMusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var gameVolume: Int = 0
    fun start(context: Context, musicResId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, musicResId)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            val sharedPrefs = context.getSharedPreferences("AppSettings", MODE_PRIVATE)
            gameVolume = sharedPrefs.getInt("gameVolume", 50)
            mediaPlayer?.setVolume(gameVolume / 100f, gameVolume / 100f)
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
        mediaPlayer?.setVolume(gameVolume / 100f, gameVolume / 100f)
        mediaPlayer?.start()
    }

    fun setVolume(volume: Int) {
        mediaPlayer?.setVolume(volume / 100f, volume / 100f)
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}