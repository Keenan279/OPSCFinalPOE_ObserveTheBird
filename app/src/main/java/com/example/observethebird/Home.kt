package com.example.observethebird

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.observethebird.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {
    private lateinit var sound: Sound

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sound = Sound(this)

        // Runs when the VIEW BIRD MAP LAYOUT is clicked
        binding.viewBirdMapLayout.setOnClickListener()
        {
            // Intent to take the user to the View Bird Map activity (From IntentHelper)
            ClassIntent(this, ViewBirdMap::class.java)
            sound.playSound()
        }

        // Runs when the SAVE BIRD OBSERVATION LAYOUT is clicked
        binding.saveBirdObservationLayout.setOnClickListener()
        {
            // Intent to take the user to the Save Bird Observation activity (From IntentHelper)
            ClassIntent(this, SaveBirdObservation::class.java)
            sound.playSound()

        }

        // Runs when the VIEW OBSERVATIONS LAYOUT is clicked
        binding.viewObservationsLayout.setOnClickListener()
        {
            // Intent to take the user to the View Observations activity (From IntentHelper)
            ClassIntent(this, ViewAllObservations::class.java)
            sound.playSound()

        }

        // Runs when the VIEW BADGES LAYOUT is clicked
        binding.badgesLayout.setOnClickListener()
        {
            // Intent to take the user to the View Badges activity (From IntentHelper)
            ClassIntent(this, UserBadges::class.java)
            sound.playSound()

        }

        // Runs when the SETTINGS LAYOUT is clicked
        binding.settingsLayout.setOnClickListener()
        {
            // Intent to take the user to the Settings activity (From IntentHelper)
            ClassIntent(this, Settings::class.java)
            sound.playSound()

        }

        // Runs when the LOGOUT LAYOUT is clicked
        binding.logOutLayout.setOnClickListener()
        {
            // Intent to take the user to the Login activity (From IntentHelper)
            ClassIntent(this, MainActivity::class.java)
            sound.playSound()

        }
    }
}