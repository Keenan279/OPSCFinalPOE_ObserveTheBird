package com.example.observethebird

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import com.example.observethebird.databinding.ActivityUserBadgesBinding
import com.example.observethebird.databinding.ActivityViewAllObservationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserBadges : AppCompatActivity() {

    // Database implementation and variables
    private val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Observations/${UserSingleton.username}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUserBadgesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Observations>()

                // Iterating over the children in the list
                for (pulledObservation in snapshot.children) {
                    val observation: Observations? = pulledObservation.getValue(Observations::class.java)
                    if (observation != null) {
                        // Adding the values from the database into the list
                        list.add(observation)
                    }
                }

                // Getting the currently logged in users username
                val desiredUsername = UserSingleton.username

                // Creating a filtered list to only store the observations with the currently logged in users username
                val filteredList = list.filter { observation -> observation.Username == desiredUsername }

                // Getting the count of observations
                val observationCount = filteredList.size

                // Checking the conditions and awarding the badges accordingly
                if (observationCount >= 5) {
                    binding.achievementFive.text = "5/5"
                    binding.txtFiveObservations.text = "Make 5 unique  observations (Completed)"
                }
                else
                {
                    binding.achievementFive.text = "$observationCount/5"
                    binding.txtFiveObservations.text = "Make 5 unique  observations (In Progress)"
                }

                if (observationCount > 15) {
                    binding.achievementFifteen.text = "15/15"
                    binding.txtFifteenObservations.text = "Make 15 unique observations (Completed)"
                }
                else
                {
                    binding.achievementFifteen.text = "$observationCount/15"
                    binding.txtFifteenObservations.text = "Make 15 unique  observations (In Progress)"
                }

                if (observationCount > 50) {
                    binding.achievementFifty.text = "50/50"
                    binding.txtFiftyObservations.text = "Make 50 unique observations (Completed)"
                }
                else
                {
                    binding.achievementFifty.text = "$observationCount/50"
                    binding.txtFiftyObservations.text = "Make 50 unique observations (In Progress)"
                }

                // Setting the amount of observations text
                binding.txtAmountOfObservations.text = "Total Observations: $observationCount"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UserBadges,
                    "Error reading from database",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Home activity (From IntentHelper)
            ClassIntent(this, Home::class.java)
        }
    }
}