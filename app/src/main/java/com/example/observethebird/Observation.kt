package com.example.observethebird

import android.graphics.Bitmap
import java.io.Serializable

class Observations : Serializable {

    var Longitude : String
    var Latitude : String
    var Species : String
    var Colour : String
    var Date : String
    var metricOrImperial : String
    var maximumTravelDistance: String

    // Class constructor
    constructor(LongitudeIn: String, LatitudeIn : String, SpeciesIn: String, TaskStartTimeIN: String, TaskEndTimeIN : String, TaskCategoryIN: String)
    {
        Longitude = LongitudeIn
        Latitude = LatitudeIn
        Species = SpeciesIn
        Colour = ColourIn
        Date = DateIn
        metricOrImperial = metricOrImperialIn
        maximumTravelDistance = maximumTravelDistanceIn
    }
}