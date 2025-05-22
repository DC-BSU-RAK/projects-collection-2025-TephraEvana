package com.speed.swifttest

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {
    
    private lateinit var homeIcon: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var infoIcon: ImageView
    private lateinit var settingsIcon: ImageView
    private lateinit var userIcon: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Initialize bottom navigation icons
        homeIcon = findViewById(R.id.home_icon)
        profileIcon = findViewById(R.id.profile_icon)
        infoIcon = findViewById(R.id.info_icon)
        settingsIcon = findViewById(R.id.settings_icon)
        
        // Set the info icon to active since we're in the InfoActivity
        updateBottomNavIcons(infoIcon)
        
        // Setup navigation click listeners
        setupBottomNavigation()
        
        // Set up user icon click listener to navigate to developers page
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
            // Already on info, no need to navigate
            updateBottomNavIcons(infoIcon)
        }
        
        settingsIcon.setOnClickListener {
            navigateToActivity(SettingsActivity::class.java)
            updateBottomNavIcons(settingsIcon)
        }
    }
    
    private fun <T : AppCompatActivity> navigateToActivity(activityClass: Class<T>) {
        // Use javaClass instead of this::class.java for proper comparison
        if (javaClass != activityClass) {
            val intent = Intent(this, activityClass)
            startActivity(intent)
            // Optional: add transition animation
            // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            
            // Uncomment if you want to finish this activity when navigating
            // finish()
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