package com.example.observethebird

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.observethebird.databinding.ActivityEditAccountBinding
import com.example.observethebird.databinding.ActivityRegisterBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditAccount : AppCompatActivity() {

    // Database implementation and variables
    private val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Navigates the user back to the Settings Page
            ClassIntent(this, Settings::class.java)
        }

        // Runs when the CONFIRM PASSWORD BUTTON is clicked
        binding.btnConfirmPassword.setOnClickListener{
            // Saving the values entered by the user
            val password = binding.txtPassword.text.toString().trim()
            val conPassword = binding.txtConfirmPassword.text.toString().trim()

            // Checking if the text boxes are null
            if (!password.isEmpty() && !conPassword.isEmpty())
            {
                // Checking if the two passwords match
                if (password.equals(conPassword))
                {
                    updatePassword(password)
                }
                else
                {
                    Toast.makeText(this,"The passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this,"Please fill in all the values to proceed", Toast.LENGTH_SHORT).show()
            }
        }

        // Runs when the CONFIRM DETAILS BUTTON is clicked
        binding.btnConfirmDetails.setOnClickListener{
            // Saving the value entered by the user
            val number = binding.txtCellNumber.text.toString().trim()

            // Checking if the text box is null
            if (!number.isEmpty())
            {
                updateNumber(number)
            }
            else
            {
                Toast.makeText(this,"Please fill in the value to proceed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // This method updates the users password
    private fun updatePassword(password: String) {
        // Building the reference to the specific user's node
        val username = UserSingleton.username
        val userReference = myRef.child(username)

        // Linking the typed-in password to the value in the database
        val newPreference = mapOf("password" to password)

        // Displaying a success or error message
        userReference.updateChildren(newPreference).addOnSuccessListener {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "There was an error updating your password", Toast.LENGTH_SHORT).show()
        }
    }

    // This method updates the users phone number
    private fun updateNumber(number: String) {
        // Building the reference to the specific user's node
        val username = UserSingleton.username
        val userReference = myRef.child(username)

        // Linking the typed-in phone number to the value in the database
        val newPreference = mapOf("number" to number)

        // Displaying a success or error message
        userReference.updateChildren(newPreference).addOnSuccessListener {
            Toast.makeText(this, "Phone number updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "There was an error updating your phone number", Toast.LENGTH_SHORT).show()
        }
    }
}