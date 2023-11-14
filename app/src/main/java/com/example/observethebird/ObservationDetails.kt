package com.example.observethebird

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.observethebird.UserSingleton.username
import com.example.observethebird.databinding.ActivityObservationDetailsBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable

private val datePattern = """^\d{2}/\d{2}/\d{4}$"""
val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
var observationsRef = database.getReference("Observations")

data class Sighting(         //data class will be used to carry data across each activity
    val username: String,
    val latitude: Double,
    val longitude: Double,
    val species: String,
    val description: String,
    val date: String
) : Serializable

private lateinit var sound: Sound


class ObservationDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityObservationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sound = Sound(this)

        val latitude = intent.getDoubleExtra(
            "latitude",
            0.0
        )       //retrieve and set latitude and longtitude to a default value if not found
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Receives the maximumTravelDistance string value from Settings (From IntentHelper)
        var maximumTravelDistance = intent.getSerializableExtra("maximumTravelDistance") as Int?
        // Holds the values passed over from the Save Observation activity
        var observations = arrayListOf<Observation>()

        // Receives the metricOrImperial string value from Settings (From IntentHelper)
        var metricOrImperial = intent.getSerializableExtra("metricOrImperial") as String?



        binding.btnSave.setOnClickListener {                           //method runs once the data has been inputted and button is pressed
            val species = binding.txtSpecies.text.toString().trim()
            val description = binding.txtColour.text.toString().trim()
            val date = binding.txtDate.text.toString().trim()

            if (date.matches(datePattern.toRegex()) && species.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty()) {
                val sighting = Sighting(
                    UserSingleton.username,                        //retrieved from singleton
                    latitude,
                    longitude,
                    species,
                    description,
                    date
                )

                // Save the sighting to the database
                val observationKey = observationsRef.child(UserSingleton.username).push().key
                val observationUpdates = HashMap<String, Any>()
                observationUpdates["$username/$observationKey"] = sighting
                observationsRef.updateChildren(observationUpdates)

                val intent = Intent()
                intent.putExtra("sighting", sighting)             //returns user back to map holding the data
                setResult(Activity.RESULT_OK, intent)
                sound.playSound()

                finish()

                Toast.makeText(
                    this,
                    "Data added: $sighting",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Please enter all the values to proceed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Home activity (From IntentHelper)
            DataIntent(this, observations, maximumTravelDistance, metricOrImperial, Home::class.java)
        }
    }

}