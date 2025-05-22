package com.speed.swifttest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var userRepository: UserRepository
    
    // UI elements for profile display
    private lateinit var fullNameText: TextView
    private lateinit var ageGenderText: TextView
    private lateinit var birthdateText: TextView
    private lateinit var emailText: TextView
    private lateinit var addressText: TextView
    private lateinit var phoneText: TextView
    
    // Buttons
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button
    
    // Bottom navigation icons
    private lateinit var homeIcon: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var infoIcon: ImageView
    private lateinit var settingsIcon: ImageView
    
    // Session management
    private lateinit var sessionManager: SessionManager
    private var currentUsername: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize repository and session manager
        userRepository = UserRepository.getInstance(this)
        sessionManager = SessionManager.getInstance(this)
        
        // Get current logged in user
        currentUsername = sessionManager.getLoggedInUsername()
        
        // If no user is logged in, redirect to login
        if (currentUsername == null) {
            navigateToLogin()
            return
        }
        
        // Initialize UI elements
        initializeViews()
        
        // Setup navigation
        setupBottomNavigation()
        
        // Set the profile icon to active
        updateBottomNavIcons(profileIcon)
        
        // Load current user profile data
        loadUserProfile()
        
        // Setup button listeners
        setupButtonListeners()
    }
    
    private fun initializeViews() {
        // Profile information TextViews
        fullNameText = findViewById(R.id.text_full_name)
        ageGenderText = findViewById(R.id.text_age_gender)
        birthdateText = findViewById(R.id.text_birthdate)
        emailText = findViewById(R.id.text_email)
        addressText = findViewById(R.id.text_address)
        phoneText = findViewById(R.id.text_phone)
        
        // Buttons
        logoutButton = findViewById(R.id.button_logout)
        editProfileButton = findViewById(R.id.button_edit_profile)
        
        // Bottom navigation icons
        homeIcon = findViewById(R.id.home_icon)
        profileIcon = findViewById(R.id.profile_icon)
        infoIcon = findViewById(R.id.info_icon)
        settingsIcon = findViewById(R.id.settings_icon)
    }
    
    private fun loadUserProfile() {
        // Get the currently logged in user
        val user = userRepository.getUserByUsername(currentUsername!!)
        
        if (user != null) {
            // Display user information
            fullNameText.text = user.fullName
            
            // For fields not in the User model, get from UserExtendedInfo if available
            val extendedInfo = sessionManager.getUserExtendedInfo(currentUsername!!)
            
            if (extendedInfo != null) {
                ageGenderText.text = "Age: ${extendedInfo.age}\nGender: ${extendedInfo.gender}"
                birthdateText.text = "Birthdate: ${extendedInfo.birthdate}"
                emailText.text = "Email: ${extendedInfo.email}"
                addressText.text = "Address: ${extendedInfo.address}"
                phoneText.text = "Phone: ${extendedInfo.phone}"
            } else {
                // Default placeholder values if no extended info
                ageGenderText.text = "Age: \nGender: "
                birthdateText.text = "Birthdate: "
                emailText.text = "Email: "
                addressText.text = "Address: "
                phoneText.text = "Phone: "
            }
        } else {
            // User not found, possibly logged out or data corrupted
            navigateToLogin()
        }
    }
    
    private fun setupButtonListeners() {
        // Logout button functionality
        logoutButton.setOnClickListener {
            // Clear logged in user session
            sessionManager.clearSession()
            
            // Navigate to login screen
            navigateToLogin()
        }
        
        // Edit profile button functionality
        editProfileButton.setOnClickListener {
            // Navigate to edit profile screen
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun setupBottomNavigation() {
        homeIcon.setOnClickListener {
            navigateToActivity(MainActivity::class.java)
            updateBottomNavIcons(homeIcon)
        }
        
        profileIcon.setOnClickListener {
            // Already on profile, no need to navigate
            updateBottomNavIcons(profileIcon)
        }
        
        infoIcon.setOnClickListener {
            navigateToActivity(InfoActivity::class.java)
            updateBottomNavIcons(infoIcon)
        }
        
        settingsIcon.setOnClickListener {
            navigateToActivity(SettingsActivity::class.java)
            updateBottomNavIcons(settingsIcon)
        }
    }
    
    private fun <T : AppCompatActivity> navigateToActivity(activityClass: Class<T>) {
        if (javaClass != activityClass) {
            val intent = Intent(this, activityClass)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
    
    private fun updateBottomNavIcons(selectedIcon: ImageView) {
        // Reset all icons to inactive color
        homeIcon.setColorFilter(resources.getColor(android.R.color.darker_gray, theme))
        profileIcon.setColorFilter(resources.getColor(android.R.color.darker_gray, theme))
        infoIcon.setColorFilter(resources.getColor(android.R.color.darker_gray, theme))
        settingsIcon.setColorFilter(resources.getColor(android.R.color.darker_gray, theme))
        
        // Set selected icon to active color
        selectedIcon.setColorFilter(resources.getColor(android.R.color.black, theme))
    }
    
    // Refresh data when returning to this screen
    override fun onResume() {
        super.onResume()
        if (currentUsername != null) {
            loadUserProfile()
        }
    }
}