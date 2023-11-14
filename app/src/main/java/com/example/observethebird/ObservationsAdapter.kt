package com.example.observethebird

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ObservationsAdapter(context: Context, private val observations: List<Observations>) :
    ArrayAdapter<Observations>(context, R.layout.custom_list_item, observations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false)

        val speciesTextView = view.findViewById<TextView>(R.id.speciesTextView)
        val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)

        val observation = observations[position]
        speciesTextView.text = "Species: ${observation.Species}"
        descriptionTextView.text = "Description: ${observation.Description}"
        dateTextView.text = "Date: ${observation.Date}"

        return view
    }
}