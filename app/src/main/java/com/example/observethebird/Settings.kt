package com.example.observethebird

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.observethebird.databinding.ActivityRegisterBinding
import com.example.observethebird.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Home activity (From IntentHelper)
            ClassIntent(this, Home::class.java)
        }

        // Runs when the EDIT ACCOUNT LAYOUT is clicked
        binding.editAccountLayout.setOnClickListener()
        {
            // Intent to take the user to the Edit Account activity (From IntentHelper)
            ClassIntent(this, EditAccount::class.java)
        }

        // Runs when the METRIC OR IMPERIAL LAYOUT is clicked
        binding.metricOrImperialLayout.setOnClickListener()
        {
            // Intent to take the user to the Metric Or Imperial activity (From IntentHelper)
            ClassIntent(this, MetricOrImperial::class.java)
        }

        // Runs when the MAXIMUM DISTANCE LAYOUT is clicked
        binding.maximumDistanceLayout.setOnClickListener()
        {
            // Intent to take the user to the Maximum Travel Distance activity (From IntentHelper)
            ClassIntent(this, TravelDistance::class.java)
        }

        // Runs when the LOGOUT LAYOUT is clicked
        binding.logOutLayout.setOnClickListener()
        {
            // Intent to take the user to the Login activity (From IntentHelper)
            ClassIntent(this, MainActivity::class.java)
        }
    }
}