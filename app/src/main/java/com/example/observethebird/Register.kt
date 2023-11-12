package com.example.observethebird

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.observethebird.databinding.ActivityRegisterBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    // Database implementation and variables
    private val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Users")

    // Class variables
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var number: String
    private lateinit var dateofbirth: String
    private lateinit var pass: String
    private lateinit var conpass: String
    private val datePattern = """^\d{2}/\d{2}/\d{4}$"""
    private lateinit var sound: Sound


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sound = Sound(this)

        // Runs when the BACK BUTTON is clicked
        binding.backButton.setOnClickListener {
            // Intent to take the user to the Login activity using IntentHelper
            ClassIntent(this, MainActivity::class.java)
            sound.playSound()

        }

        // Runs when the REGISTER BUTTON is clicked
        binding.btnRegister.setOnClickListener {
            // Saving the values entered by the user
            username = binding.txtUsername.text.toString().trim()
            email = binding.txtEmail.text.toString().trim()
            number = binding.txtCellNumber.text.toString().trim()
            dateofbirth = binding.txtDOB.text.toString().trim()
            pass = binding.txtPassword.text.toString().trim()
            conpass = binding.txtConfirmPassword.text.toString().trim()

            // Exception handling "if loops"
            if (username.isNotEmpty() && email.isNotEmpty() && number.isNotEmpty() && dateofbirth.isNotEmpty() && pass.isNotEmpty() && conpass.isNotEmpty())
            {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    if(number.isDigitsOnly())
                    {
                        if (dateofbirth.matches(datePattern.toRegex()))
                        {
                            if (pass == conpass)
                            {
                                // Adding the users details into the database
                                val user = User(username, email, number, dateofbirth, pass, "Metric", "10")
                                myRef.child(username).setValue(user)

                                // Displaying a success message if the register was successful
                                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()

                                // Intent to take the user to the Login activity (From IntentHelperClass)
                                ClassIntent(this ,MainActivity::class.java)
                                sound.playSound()

                            }
                            else
                            {
                                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else
                        {
                            Toast.makeText(this, "Date not in the correct format", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "Not a valid phone number", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this, "Not a valid email address", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please fill in all details before continuing", Toast.LENGTH_SHORT).show()
            }
        }

        // Runs when the CLEAR BUTTON is clicked
        binding.btnClear.setOnClickListener {
            // Clearing all the text box values
            val nameBox = findViewById<EditText>(R.id.txtUsername)
            val emailBox = findViewById<EditText>(R.id.txtEmail)
            val cellBox = findViewById<EditText>(R.id.txtCellNumber)
            val dobBox = findViewById<EditText>(R.id.txtDOB)
            val passBox = findViewById<EditText>(R.id.txtPassword)
            val conpassBox = findViewById<EditText>(R.id.txtConfirmPassword)

            nameBox.text.clear()
            emailBox.text.clear()
            cellBox.text.clear()
            dobBox.text.clear()
            passBox.text.clear()
            conpassBox.text.clear()
        }
    }
}
