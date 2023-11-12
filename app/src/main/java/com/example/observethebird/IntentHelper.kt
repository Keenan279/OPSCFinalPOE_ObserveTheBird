package com.example.observethebird

import android.content.Context
import android.content.Intent

fun ClassIntent(context: Context, activityToOpen: Class<*>)       //casual traversal intent
{
    val intent = Intent(context, activityToOpen)
    context.startActivity(intent)
}

fun DataIntent(context: Context, observations:  ArrayList<Observation>, maximumTravelDistance: Int?, metricOrImperial: String?, activityToOpen: Class<*>)
{                                                                                       //a more specifc intent to carry data across forms and activities
    val intent = Intent(context, activityToOpen)
    intent.putExtra("OBSERVATIONS_LIST_EXTRA", observations)
    intent.putExtra("maximumTravelDistance", maximumTravelDistance)
    intent.putExtra("metricOrImperial", metricOrImperial)
    context.startActivity(intent)
}