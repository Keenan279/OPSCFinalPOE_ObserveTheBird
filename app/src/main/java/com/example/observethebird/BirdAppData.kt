package com.example.observethebird

data class User(                                //data class used for the user
    val Username: String? = null,
    val Email: String? = null,
    val Number: String? = null,
    val Dob: String? = null,
    val Password: String? = null,
    val MetricOrImperial: String? = null,
    val DistanceToTravel: String? = null
)

data class Observations(                    //observation class used for observations
    var Username: String = "",
    var Date: String = "",
    var Description: String = "",
    var Lattitude: Double = 0.0,
    var Longitude: Double = 0.0,
    var Species: String = ""
)
