package com.example.observethebird

import android.content.Context
import android.media.MediaPlayer


class Sound(private val context: Context) {

    private var mp: MediaPlayer = MediaPlayer()

    init {
        // Initialize MediaPlayer with the sound file
        mp = MediaPlayer.create(context, R.raw.audiogameclick)
    }

    // Function to play the sound
    fun playSound() {
        // Check if the MediaPlayer is playing, and stop it to restart from the beginning
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
            // Initialize MediaPlayer again to reset it
            mp = MediaPlayer.create(context, R.raw.audiogameclick)
        }
        // Start playing the sound
        mp.start()
    }
}
