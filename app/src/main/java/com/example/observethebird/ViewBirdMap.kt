package com.example.observethebird

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.observethebird.databinding.ActivityEditAccountBinding
import com.example.observethebird.databinding.ActivityViewBirdMapBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@Suppress("DEPRECATION")
class ViewBirdMap : AppCompatActivity() {

    //below are the two API keys needed for this project
    private val eBirdApiKey= "tib2aind6e4d"
    private val directionsApiKey = "AIzaSyB9vPuFXqCHMSmpR1BBr5cNOiphcXhMP_c"

    private var currentPolyline: Polyline? = null       //sets polyline to null value
    private var maximumTravelDistance: Int = 2          //default setting for travel distance

    private var chosenOption: String =" Miles"      //Default choice

    private var distanceToTravel : Double? = 0.0             //intialise to zero before create





    data class Hotspot(val name: String, val latitude: Double, val longitude: Double)     //class for hotspots
    private lateinit var googleMap: GoogleMap              //implmentation for map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewBirdMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ******************************* CLASS VARIABLES *****************************************
        // Receives the maximumTravelDistance string value from Settings (From IntentHelper)
        var maximumTravelDistance = intent.getSerializableExtra("maximumTravelDistance") as Int?
        // Holds the values passed over from the Save Observation activity
        var observations = arrayListOf<Observation>()

        // Receives the metricOrImperial string value from Settings (From IntentHelper)
        var metricOrImperial = UserSingleton.metricOrImperial

        // *****************************************************************************************


        // Obtaining the observations arrayList values from the Data intent
        if (intent.getSerializableExtra("OBSERVATIONS_LIST_EXTRA")!=null)
        {
            observations = intent.getSerializableExtra("OBSERVATIONS_LIST_EXTRA") as ArrayList<Observation>
        }

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Home activity (From IntentHelper)
            DataIntent(this, observations, maximumTravelDistance, metricOrImperial, Home::class.java)
        }

        if(maximumTravelDistance == null)
        {
            maximumTravelDistance = 1
        }

        if(metricOrImperial == "Metric")
        {
            // Using this line to convert the miles to km to display on the map
            distanceToTravel = maximumTravelDistance?.times(1.60934)
            chosenOption=" Km"
        }

        if(metricOrImperial == "Imperial")
        {
            distanceToTravel = maximumTravelDistance?.times(1.0)
            chosenOption=" Miles"
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED   //checks if permissions on the device are granteed
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),         //permissions are granted
                1
            )
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            // Customize map settings
            it.uiSettings.isZoomControlsEnabled = true
            it.uiSettings.isCompassEnabled = true

            // Add a marker for the current location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation             //finds most recent and last location
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.addMarker(
                            MarkerOptions().position(currentLatLng).title("Current Location")       //stores the current location and displays it on the map with a marker
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        getNearbySpots(location.latitude, location.longitude)        //gets the nearby hotpots
                    }
                }
        }
    }

    private fun getNearbySpots(
        latitude: Double,
        longitude: Double,
        radiusInKm: Int = maximumTravelDistance         //value is found in the settings and is brought here using an intent
    ) {
        val url = "https://api.ebird.org/v2/data/obs/geo/recent?lat=$latitude&lng=$longitude&dist=$radiusInKm"  //url including the parameters

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("X-eBirdApiToken", eBirdApiKey)
            .build()         //builds request and is retrieved from eBirdApi

        client.newCall(request).enqueue(object : Callback {  //this block of code is making an HTTP request,
                                                             // when the response is received, it processes the response using the parseHotspotResponse function on the main thread T
                                                             // ensuring that UI-related operations are performed correctly
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()

                runOnUiThread {
                    parseHotspotResponse(responseBody)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()                         //the request code ends here
            }
        })
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun parseHotspotResponse(response: String?) {
        val hotspots = mutableListOf<Hotspot>()         //this creates a list so that the hotspots can be parsed
        val jsonArray = JSONArray(response)             //stored in a Json Array

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

                // desired width and height
                val width = 50 // set the desired width
                val height = 50 // set the desired height

                // Create a scaled bitmap
                val scaledBitmap = Bitmap.createScaledBitmap(birdIcon, width, height, false)

                // Convert the scaled bitmap to a BitmapDescriptor
                val scaledIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(hotspotLocation)
                        .title(hotspot.name)
                        .icon(scaledIcon)
                )

                // Set the hotspot as the marker's tag
                marker?.tag = hotspot

                googleMap.setOnMarkerClickListener { clickedMarker ->
                    val destination = clickedMarker.position

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED   //if the location permission is granted
                    ) {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    val origin = LatLng(location.latitude, location.longitude)
                                    requestDirections(origin, destination)        //we will request directions from the current location to the hotspot

                                    val clickedHotspot = clickedMarker.tag as Hotspot
                                    Toast.makeText(this, "Clicked on hotspot: ${clickedHotspot.name}", Toast.LENGTH_SHORT).show()
                                    currentPolyline?.remove()             //the above lines triggers when a hotspot is clicked on

                                    val alertDialog = AlertDialog.Builder(this)
                                        .setTitle("Calculate distance to this particular hotspot?") //asks if the user wants to calculate distance
                                        .setPositiveButton("Yes") { dialog, _ ->
                                            //'Yes' button click
                                            dialog.dismiss()
                                            requestDirections(origin, destination)

                                            // Calculate and display travel time
                                            val context = GeoApiContext.Builder()
                                                .apiKey(directionsApiKey)
                                                .build()

                                            GlobalScope.launch(Dispatchers.IO) {
                                                val directions = DirectionsApi.getDirections(context, "${origin.latitude},${origin.longitude}",
                                                    "${destination.latitude},${destination.longitude}")
                                                    .mode(TravelMode.WALKING)
                                                    .await()  //travel mode is set to walking but can be changed to driving with a change of a method

                                                val route = directions.routes[0] // using the route
                                                val travelTime = route.legs.sumOf { it.duration.inSeconds.toInt() } / 60 // Convert seconds to minutes

                                                val distanceInKm = haversine(origin.latitude, origin.longitude, destination.latitude, destination.longitude) //for metric
                                                val roundedDistance = String.format("%.2f", distanceInKm) //distance is rounded off

                                                runOnUiThread {     //the below toasts show the esimtaed walking time as well as the distance from the current point to the hotspot
                                                    Toast.makeText(this@ViewBirdMap, "Estimated Travel Time To Walk: $travelTime minutes", Toast.LENGTH_SHORT).show()
                                                    Toast.makeText(this@ViewBirdMap, "Distance: $roundedDistance "+chosenOption, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        .setNegativeButton("No") { dialog, _ ->
                                            dialog.dismiss()
                                            currentPolyline?.remove()             //if the user says no, the polyline is removed from the map
                                            // Ask if the user wants to return to main menu
                                            AlertDialog.Builder(this)
                                                .setTitle("Return to Main Menu?")
                                                .setPositiveButton("Yes") { dialog, _ ->
                                                    dialog.dismiss()
                                                    val mainIntent = Intent(this, Home::class.java)     //returns the user back to the main menu
                                                    mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    startActivity(mainIntent)   //activity is started and user is redirected
                                                    finish()
                                                }
                                                .setNegativeButton("No") { dialog, _ ->
                                                    dialog.dismiss()
                                                }
                                                .show()
                                        }
                                        .show()
                                } else {

                                }
                            }
                    } else {

                    }

                    true
                }
            }

        }
    }

    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {                    //taken from https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
        val R = 6371 // Radius of the Earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon/2) * Math.sin(dLon/2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
        return R * c          //this formula calculates the distance from two points using the radius of the earth
    }

    private fun requestDirections(origin: LatLng, destination: LatLng) {
        val context = GeoApiContext.Builder()
            .apiKey(directionsApiKey)
            .build()  //builds direction context so that polylines can be drawn and estimations can be made

        GlobalScope.launch(Dispatchers.IO) {
            val directions = DirectionsApi.getDirections(context, "${origin.latitude},${origin.longitude}",
                "${destination.latitude},${destination.longitude}")
                .mode(TravelMode.DRIVING)
                .await()   //builds directions scope

            runOnUiThread {
                displayDirections(directions)   //displays the direction and obtains fastest route once method is finished
            }
        }
    }

    private fun displayDirections(directions: DirectionsResult) {
        val route = directions.routes[0] // using the first route
        currentPolyline?.remove()     //removes old polyline
        val overviewPolyline = route.overviewPolyline
        val decodedPoints = PolyUtil.decode(overviewPolyline.encodedPath)

        val polylineOptions = PolylineOptions()
        polylineOptions.addAll(decodedPoints)
        polylineOptions.color(Color.BLUE)               //above code defines the fastest route and sets polyline to blue
      currentPolyline = googleMap.addPolyline(polylineOptions)

        // Move camera to show the entire route
        val boundsBuilder = LatLngBounds.builder()
        decodedPoints.forEach { boundsBuilder.include(it) }

        val bounds = boundsBuilder.build()
        val padding = 100 // Padding in pixel count
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.animateCamera(cameraUpdate) //moves camera
    }
}