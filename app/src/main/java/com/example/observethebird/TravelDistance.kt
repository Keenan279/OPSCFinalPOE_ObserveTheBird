package com.example.observethebird

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.observethebird.databinding.ActivityTravelDistanceBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class TravelDistance : AppCompatActivity() {

    // Database implementation and variables
    private val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Users")
    private lateinit var sound: Sound

    private lateinit var currentUser : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTravelDistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sound = Sound(this)
        // Runs when the CONFIRM BUTTON is clicked
        binding.btnConfirm.setOnClickListener{
            sound.playSound()
            // Saving the value entered by the user
            val stringDistance = binding.txtDistance.text.toString().trim()

            if(!stringDistance.isEmpty())
            {
                if(stringDistance.isDigitsOnly())
                {
                    // Method to update the travel distance value in the database
                    updateDistance(stringDistance)
                    UserSingleton.maximumTravelDistance =  stringDistance.toInt()

                }
                else
                {
                    Toast.makeText(this,"Please enter a valid distance", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this,"Please enter in a value to proceed", Toast.LENGTH_SHORT).show()
            }
        }

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Settings activity (From IntentHelper)
            ClassIntent(this, Settings::class.java)
        }
    }

    // This method updates the users maximum travel distance
    private fun updateDistance(distance: String) {
        // Building the reference to the specific user's node
        val username = UserSingleton.username
        val userReference = myRef.child(username)

        // Linking the typed-in distance to the value in the database
        val newPreference = mapOf("distanceToTravel" to distance)

        // Displaying a success or error message
        userReference.updateChildren(newPreference).addOnSuccessListener {
            // Confirmation message
            Toast.makeText(this,"New maximum travel distance saved", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "There was an error updating your password", Toast.LENGTH_SHORT).show()
        }
    }
}

