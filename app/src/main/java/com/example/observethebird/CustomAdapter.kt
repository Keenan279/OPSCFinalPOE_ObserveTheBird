import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.observethebird.R
import com.example.observethebird.Sighting
import java.util.ArrayList

class CustomAdapter(context: Context, private val sightings: List<Sighting>) :            //this adapter will be used to display the data into a list
    ArrayAdapter<Sighting>(context, R.layout.custom_list_item, sightings) {                            //store the observation sightings

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false)

        val speciesTextView = view.findViewById<TextView>(R.id.speciesTextView)                                      //these val's extract the values from the textviews
        val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)

        val sighting = sightings[position]                                                                        //the specific sighting and cycling through an index
        speciesTextView.text = "Species: ${sighting.species}"
        descriptionTextView.text = "Description: ${sighting.description}"
        dateTextView.text = "Date: ${sighting.date}"

        return view
    }
}

