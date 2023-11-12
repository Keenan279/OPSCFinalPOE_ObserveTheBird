package com.example.observethebird

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.observethebird.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Database implementation and variables
    private val database = Firebase.database("https://observethebird-eb5e1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Users")
    private lateinit var sound: Sound

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sound = Sound(this)

        // Runs when the REGISTER BUTTON is clicked
        binding.btnGoToRegister.setOnClickListener {
            // Navigates the user to the Register Page
            ClassIntent(this, Register::class.java)
            sound.playSound()

        }

        // Runs when the LOGIN BUTTON is clicked
        binding.btnLogin.setOnClickListener {
            val username = binding.txtUsername.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()

            if(username.isNotEmpty() && password.isNotEmpty())
            {
                // Running the loginUser method with the entered in values
                loginUser(username, password)
            }
            else
            {
                // Error message if no values were entered in the text boxes
                Toast.makeText(this, "Please fill in all details before continuing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //--------------------------------------------------------------------------------------------//

    // Method to check if the values entered match the values stored in the database
    private fun loginUser(username: String, password: String) {

        myRef.child(username).get().addOnSuccessListener {

            // Retrieving the username and password from the database
            val userNameHere = it.child("username").value.toString()
            val passwordHere = it.child("password").value

            if (password == passwordHere){
                // Saving the username of the currently logged in user
                UserSingleton.username = userNameHere

                // Navigating the user to the Home Page if login was successful
                ClassIntent(this, Home::class.java)
                sound.playSound()

            }
            else
            {
                Toast.makeText(this, "Username or password is incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
