package com.speed.swifttest;


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordText: TextView
    private lateinit var createAccountText: TextView
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager.getInstance(this)
    if (sessionManager.getLoggedInUsername() != null) {
        // User is already logged in, redirect to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Close LoginActivity so user can't go back to it
        return
    }
        setContentView(R.layout.activity_login)

        userRepository = UserRepository.getInstance(this)

        // Initialize views
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        forgotPasswordText = findViewById(R.id.textViewForgotPassword)
        createAccountText = findViewById(R.id.textViewCreateAccount)

        // Set up login button click listener
        loginButton.setOnClickListener {
            loginUser()
        }

        // Set up create account text click listener
        createAccountText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Set up forgot password text click listener
        forgotPasswordText.setOnClickListener {
            // Implement forgot password functionality
            Toast.makeText(this, "Forgot password functionality to be implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser() {
    val username = usernameEditText.text.toString().trim()
    val password = passwordEditText.text.toString()

    // Validate input fields
    if (username.isEmpty() || password.isEmpty()) {
        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        return
    }

    // Attempt to authenticate the user
    val isAuthenticated = userRepository.authenticateUser(username, password)

    if (isAuthenticated) {
        // Save the logged in user's session
        val sessionManager = SessionManager.getInstance(this)
        sessionManager.setLoggedInUser(username)

        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        // Navigate to main screen
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    } else {
        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
    }
}
}