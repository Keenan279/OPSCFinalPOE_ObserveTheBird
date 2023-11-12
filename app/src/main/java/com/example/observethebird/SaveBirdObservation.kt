package com.example.observethebird

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.observethebird.databinding.ActivityViewBirdMapBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SaveBirdObservation : AppCompatActivity() {
    private val eBirdApiKey = "tib2aind6e4d"        //api key from eBird
    var binocularsMarker: Marker? = null
    private val sightings = mutableListOf<Sighting>()              //create list for storage

    data class Hotspot(val name: String, val latitude: Double, val longitude: Double)        //data class that will show + store hotspot details

    private var googleMap: GoogleMap? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewBirdMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Receives the maximumTravelDistance string value from Settings (From IntentHelper)
        var maximumTravelDistance = intent.getSerializableExtra("maximumTravelDistance") as Int?
        // Holds the values passed over from the Save Observation activity
        var observations = arrayListOf<Observation>()

        // Receives the metricOrImperial string value from Settings (From IntentHelper)
        var metricOrImperial = intent.getSerializableExtra("metricOrImperial") as String?


        // Get the username of the currently logged-in user from UserSingleton
        val username = UserSingleton.username

        // Reference to the observations for the logged-in user
        val userObservationsRef = database.getReference("Observations/$username")

        userObservationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val latitude = childSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = childSnapshot.child("longitude").getValue(Double::class.java)

                    if (latitude != null && longitude != null) {
                        // Add binoculars marker for each saved observation
                        val binocularsIcon = BitmapFactory.decodeResource(resources, R.drawable.binoccy)
                        val scaledBitmap = Bitmap.createScaledBitmap(binocularsIcon, 50, 50, false)
                        val scaledIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                        val observationLatLng = LatLng(latitude, longitude)
                        binocularsMarker = googleMap?.addMarker(
                            MarkerOptions()
                                .position(observationLatLng)
                                .title("Observation Point")
                                .icon(scaledIcon)
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Home activity (From IntentHelper)
            DataIntent(this, observations, maximumTravelDistance, metricOrImperial, Home::class.java)
        }


    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)         //does a check to see if location permissions are granted
            != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),         //emulator or phone has permissions granted
                1
            )
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            // Customize map settings here
            it.uiSettings.isZoomControlsEnabled = true
            it.uiSettings.isCompassEnabled = true

            // Get user's current location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation                                      //last known location
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap?.addMarker(
                            MarkerOptions().position(currentLatLng).title("Current Location")             //sets a red marker on the user's current location
                        )
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))             //15f is the zoom level of the map
                        val url =
                            "https://api.ebird.org/v2/data/obs/geo/recent?lat=${location.latitude}&lng=${location.longitude}&dist=5"          //build API url
                        getNearbySpots(url)
                        googleMap?.setOnMapClickListener { clickedLatLng ->

                            // Add a binoculars icon at the clicked location
                            val binocularsIcon = BitmapFactory.decodeResource(resources, R.drawable.binoccy)
                            val scaledBitmap = Bitmap.createScaledBitmap(binocularsIcon, 50, 50, false)
                            val scaledIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                            binocularsMarker=  googleMap?.addMarker(                    //adding a marker to the map and labelling it as an observation point if the user clicks on it again
                                MarkerOptions()
                                    .position(clickedLatLng)
                                    .title("Observation Point")
                                    .icon(scaledIcon)
                            )

                            // Display dialog box
                            AlertDialog.Builder(this)
                                .setTitle("Would you like to save an observation?")           //ask the user if they want to save the observation of the binoculars
                                .setPositiveButton("Yes") { dialog, _ ->

                                    dialog.dismiss()                                    //dialog box is closed


                                    val intent = Intent(this, ObservationDetails::class.java)         // Navigates to the activity where user enters details
                                    intent.putExtra("latitude", clickedLatLng.latitude)
                                    intent.putExtra("longitude", clickedLatLng.longitude)                  //carry over the coordinates
                                    startActivityForResult(intent,1)
                                }
                                .setNegativeButton("No") { dialog, _ ->                   //if no, then the dialog is removed
                                    dialog.dismiss()
                                    binocularsMarker?.remove()                                //and so too is the marker being removeed

                                    // Ask the user  to return to main menu
                                    AlertDialog.Builder(this)
                                        .setTitle("View All Observations?")
                                        .setPositiveButton("Yes") { dialog, _ ->           //if user says yes to seeing the observations, the dialog box closes
                                            dialog.dismiss()
                                            if (sightings.isNotEmpty()) {
                                                val mainIntent = Intent(this@SaveBirdObservation, ViewAllObservations::class.java)    //view all observations is opened
                                                mainIntent.putExtra("sightings", ArrayList(sightings))                                  //intent is used to traverse and carry data across
                                                startActivity(mainIntent)                                                  //activity/intent is started
                                                finish()
                                            } else {

                                                val mainIntent = Intent(this@SaveBirdObservation, ViewAllObservations::class.java)    //view all observations is opened
                                                mainIntent.putExtra("sightings", ArrayList(sightings))                                  //intent is used to traverse and carry data across
                                                startActivity(mainIntent)                                                  //activity/intent is started
                                                finish()

                                            }
                                        }
                                        .setNegativeButton("No") { dialog, _ ->
                                            dialog.dismiss()       //dialog is dismissed and map is shown
                                        }
                                        .show()
                                }
                                .show()
                        }

                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {             //this will be called when the map is displyed after the observation has been added
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val sighting = data.getSerializableExtra("sighting") as? Sighting        //obtain the sighting object
            if (sighting != null) {
                Log.d("Debug", "Received Sighting: $sighting")             //debug to see if data is indeed being carried across


                sightings.add(sighting)                                        //adds to sighting list

                // Add a binoculars marker at the saved location
                val binocularsIcon = BitmapFactory.decodeResource(resources, R.drawable.binoccy)
                val scaledBitmap = Bitmap.createScaledBitmap(binocularsIcon, 50, 50, false)
                val scaledIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                googleMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(sighting.latitude, sighting.longitude))
                        .title("Observation Point")                      //keeps observation point
                        .icon(scaledIcon)
                )
            } else {

            }
        }

        // Show a message if sightings is empty
        if (sightings.isEmpty()) {
            Toast.makeText(this, "No sightings available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNearbySpots(url: String = "https://api.ebird.org/v2/data/obs/geo/recent?lat=-33.97284124022742&lng=18.46959964611682&dist=5") {  //same code that's being used in ViewBirdMap to get hotspots
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("X-eBirdApiToken", eBirdApiKey).build()             //builds request and is retrieved from eBirdApi

        client.newCall(request).enqueue(object : Callback {  //this block of code is making an HTTP request,
                                                            // when the response is received, it processes the response using the parseHotspotResponse function on the main thread T
                                                            // ensuring that UI-related operations are performed correctly
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()

                runOnUiThread {
                    parseHotspotResponse(responseBody)
                }
            }

            override fun onFailure(call: Call, e: IOException) {           //the request code ends here
                e.printStackTrace()
            }
        })
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun parseHotspotResponse(response: String?) {
        val hotspots = mutableListOf<Hotspot>()                 //this creates a list so that the hotspots can be parsed
        val jsonArray = JSONArray(response)                //stored in a Json Array

        for (i in 0 until jsonArray.length()) {
            val jsonHotspot: JSONObject = jsonArray.getJSONObject(i)
            val name = jsonHotspot.getString("locName")
            val latitude = jsonHotspot.getDouble("lat")
            val longitude = jsonHotspot.getDouble("lng")

            hotspots.add(Hotspot(name, latitude, longitude))             //the above code as well as this current line builds the hotspot
        }

        runOnUiThread {
            hotspots.forEach { hotspot ->
                val hotspotLocation = LatLng(hotspot.latitude, hotspot.longitude)

                // custom marker icon
                val birdIcon = BitmapFactory.decodeResource(resources, R.drawable.birdyicon)

                // Defines the desired width and height of the icon
                val width = 50 // the desired width
                val height = 50 //  the desired height

                // Creates a scaled bitmap
                val scaledBitmap = Bitmap.createScaledBitmap(birdIcon, width, height, false)

                // Converts the scaled bitmap to a BitmapDescriptor
                val scaledIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                val marker = googleMap?.addMarker(           //adds marker to the map
                    MarkerOptions()
                        .position(hotspotLocation)
                        .title(hotspot.name)
                        .icon(scaledIcon)
                )

                // Set the hotspot as the marker's tag
                marker?.tag = hotspot


            }
        }
    }
}