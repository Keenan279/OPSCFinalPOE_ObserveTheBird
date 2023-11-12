package com.example.observethebird

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.observethebird.databinding.ActivityMetricOrImperialBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MetricOrImperial : AppCompatActivity() {

    // Database implementation and variables
    private val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Users")
    private lateinit var sound: Sound

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val binding = ActivityMetricOrImperialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sound = Sound(this)
        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Navigates the user back to the Settings Page
            ClassIntent(this, Settings::class.java)
            sound.playSound()
        }

        // Runs when the METRIC LAYOUT is clicked
        binding.layoutMetric.setOnClickListener()
        {
            // Running the UpdatePreference function
            updatePreference("Metric")
        }

        // Runs when the IMPERIAL LAYOUT is clicked
        binding.layoutImperial.setOnClickListener()
        {
            // Running the UpdatePreference function
            updatePreference("Imperial")
        }
    }

    // This method updates the users measuring system preference
    private fun updatePreference(metricOrImperial: String) {
        // Building the reference to the specific user's node
        val username = UserSingleton.username
        val userReference = myRef.child(username)

        // Linking the metricOrImperial choice to the value in the database
        val newPreference = mapOf("metricOrImperial" to metricOrImperial)

        // Displaying a success or error message
        userReference.updateChildren(newPreference).addOnSuccessListener {
            Toast.makeText(this, "System preference updated", Toast.LENGTH_SHORT).show()
            UserSingleton.metricOrImperial = metricOrImperial
            sound.playSound()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "There was an error updating your preference", Toast.LENGTH_SHORT).show()
        }
    }
}