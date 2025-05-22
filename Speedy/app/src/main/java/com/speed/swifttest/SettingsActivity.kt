package com.speed.swifttest

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    
    // UI elements
   // private lateinit var appearanceOption: TextView
    private lateinit var logoutOption: TextView
    
    // Bottom navigation icons
    private lateinit var homeIcon: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var infoIcon: ImageView
    private lateinit var settingsIcon: ImageView
    
    // Session management
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        // Initialize session manager
        sessionManager = SessionManager.getInstance(this)
        
        // Initialize views
        initializeViews()
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Set the settings icon to active since we're in the SettingsActivity
        updateBottomNavIcons(settingsIcon)
        
        // Setup click listeners for settings options
        setupClickListeners()
    }
    
    private fun initializeViews() {
        // Settings options
     //   appearanceOption = findViewById(R.id.appearance_option)
        logoutOption = findViewById(R.id.logout_option)
        
        // Bottom navigation icons
        homeIcon = findViewById(R.id.home_icon)
        profileIcon = findViewById(R.id.profile_icon)
        infoIcon = findViewById(R.id.info_icon)
        settingsIcon = findViewById(R.id.settings_icon)
    }
    
    private fun setupClickListeners() {
        // Appearance option click listener
     /*   appearanceOption.setOnClickListener {
            showAppearanceDialog()
        }
        */
        
        // Logout option click listener
        logoutOption.setOnClickListener {
            logoutUser()
        }
    }
    
    private fun showAppearanceDialog() {
        val options = arrayOf("Light Mode", "Dark Mode")
        
        AlertDialog.Builder(this)
            .setTitle("Choose Theme")
            .setSingleChoiceItems(options, getCurrentThemeMode()) { dialog, which ->
                when (which) {
                    0 -> setLightMode()
                    1 -> setDarkMode()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun getCurrentThemeMode(): Int {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> 1 // Dark mode
            else -> 0 // Light mode
        }
    }
    
    private fun setLightMode() {
        // Apply light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // You might want to save this preference
    }
    
    private fun setDarkMode() {
        // Apply dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        // You might want to save this preference
    }
    
    private fun logoutUser() {
        // Show confirmation dialog before logout
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                // Clear user session
                sessionManager.clearSession()
                
                // Navigate to login activity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun setupBottomNavigation() {
        homeIcon.setOnClickListener {
            navigateToActivity(MainActivity::class.java)
            updateBottomNavIcons(homeIcon)
        }
        
        profileIcon.setOnClickListener {
            navigateToActivity(ProfileActivity::class.java)
            updateBottomNavIcons(profileIcon)
        }
        
        infoIcon.setOnClickListener {
            navigateToActivity(InfoActivity::class.java)
            updateBottomNavIcons(infoIcon)
        }
        
        settingsIcon.setOnClickListener {
            // Already in settings, no need to navigate
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
}