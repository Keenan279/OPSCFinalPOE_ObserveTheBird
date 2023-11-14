package com.example.observethebird

import java.io.Serializable

class Observation : Serializable {       //serializable class that will be used to carry data across activities

    var Longitude : Double
    var Latitude : Double
    var Species : String
    var Colour : String
    var Date : String

    // Class constructor
    constructor(LongitudeIn: Double, LatitudeIn : Double, SpeciesIn: String, ColourIn: String, DateIn : String)
    {
        Longitude = LongitudeIn
        Latitude = LatitudeIn
        Species = SpeciesIn
        Colour = ColourIn
        Date = DateIn
    }
}