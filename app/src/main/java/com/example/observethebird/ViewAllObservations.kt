package com.example.observethebird

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.observethebird.databinding.ActivityViewAllObservationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewAllObservations : AppCompatActivity() {

    // Database implementation and variables
    private val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Observations/${UserSingleton.username}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewAllObservationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Binding the listview object from the xml code
        val listView = binding.sightingListView

        // Receives the maximumTravelDistance string value from Settings (From IntentHelper)
        var maximumTravelDistance = intent.getSerializableExtra("maximumTravelDistance") as Int?
        // Holds the values passed over from the Save Observation activity
        var observations = arrayListOf<Observation>()

        // Receives the metricOrImperial string value from Settings (From IntentHelper)
        var metricOrImperial = intent.getSerializableExtra("metricOrImperial") as String?



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

                // Creating the adapter to display the items
                val observationAdapter = ObservationsAdapter(this@ViewAllObservations, filteredList)
                listView.adapter = observationAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ViewAllObservations,
                    "Error reading from database",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Runs when an item in the ListView is clicked
        listView.setOnItemClickListener(AdapterView.OnItemClickListener { _, _, position, _ ->

            val selectedObservation = listView.getItemAtPosition(position) as Observations

        })

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Home activity (From IntentHelper)
            DataIntent(this, observations, maximumTravelDistance, metricOrImperial, Home::class.java)
        }
    }
}