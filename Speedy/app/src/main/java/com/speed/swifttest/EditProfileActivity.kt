package com.speed.swifttest

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Activity for editing user profile information
 */
class EditProfileActivity : AppCompatActivity() {
    
    // UI Components
    private lateinit var fullNameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var birthdateTextView: TextView
    private lateinit var calendarButton: ImageButton
    private lateinit var genderSpinner: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var doneButton: Button
    private lateinit var backButton: ImageButton
    
    // Bottom navigation icons
    private lateinit var homeIcon: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var infoIcon: ImageView
    private lateinit var settingsIcon: ImageView
    
    // Gender options for spinner
    private val genderOptions = arrayOf("Select Gender", "Male", "Female", "Non-binary", "Prefer not to say")
    
    // Data management
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private var currentUsername: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        
        // Initialize repository and session manager
        userRepository = UserRepository.getInstance(this)
        sessionManager = SessionManager.getInstance(this)
        
        // Get current logged in user
        currentUsername = sessionManager.getLoggedInUsername()
        
        // If no user is logged in, finish activity
        if (currentUsername == null) {
            finish()
            return
        }
        
        // Initialize views
        initializeViews()
        
        // Set up navigation
        setupBottomNavigation()
        
        // Set the profile icon to active since we're in the profile section
        updateBottomNavIcons(profileIcon)
        
        // Load user data into form
        loadUserData()
        
        // Set up button listeners
        setupButtonListeners()
    }
    
    private fun initializeViews() {
        // Edit text fields
        fullNameEditText = findViewById(R.id.edit_fullname)
        ageEditText = findViewById(R.id.edit_age)
        emailEditText = findViewById(R.id.edit_email)
        addressEditText = findViewById(R.id.edit_address)
        phoneEditText = findViewById(R.id.edit_phone)
        
        // Birthdate components
        birthdateTextView = findViewById(R.id.text_birthdate)
        calendarButton = findViewById(R.id.calendar_button)
        
        // Set up date picker
        birthdateTextView.setOnClickListener { showDatePicker() }
        calendarButton.setOnClickListener { showDatePicker() }
        
        // Gender spinner
        genderSpinner = findViewById(R.id.spinner_gender)
        setupGenderSpinner()
        
        // Buttons
        doneButton = findViewById(R.id.button_done_editing)
        backButton = findViewById(R.id.back_button)
        
        // Bottom navigation icons
        homeIcon = findViewById(R.id.home_icon)
        profileIcon = findViewById(R.id.profile_icon)
        infoIcon = findViewById(R.id.info_icon)
        settingsIcon = findViewById(R.id.settings_icon)
    }
    
    private fun setupGenderSpinner() {
        // Create adapter with custom dropdown view
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            genderOptions
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                if (view is TextView) {
                    view.setTextColor(Color.WHITE)
                    view.text = if (position == 0 && !genderSpinner.isPressed) "" else genderOptions[position]
                }
                return view
            }
            
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (view is TextView) {
                    view.setTextColor(Color.WHITE)
                    view.setBackgroundColor(Color.BLACK)
                    view.setPadding(16, 16, 16, 16)
                }
                return view
            }
        }
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        
        // Parse existing date if available
        val currentDate = birthdateTextView.text.toString()
        if (currentDate.isNotEmpty() && currentDate != "Birthdate") {
            try {
                val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                val date = sdf.parse(currentDate)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                birthdateTextView.text = selectedDate
                birthdateTextView.setTextColor(resources.getColor(android.R.color.white, theme))
            },
            year, month, day
        )
        
        datePickerDialog.show()
    }
    
    private fun loadUserData() {
        // Get the currently logged in user
        val user = userRepository.getUserByUsername(currentUsername!!)
        
        if (user != null) {
            // Set full name from User object
            fullNameEditText.setText(user.fullName)
            
            // Get extended info if available
            val extendedInfo = sessionManager.getUserExtendedInfo(currentUsername!!)
            
            if (extendedInfo != null) {
                // Set extended info fields
                ageEditText.setText(extendedInfo.age)
                
                // Set birthdate
                if (extendedInfo.birthdate.isNotEmpty()) {
                    birthdateTextView.text = extendedInfo.birthdate
                    birthdateTextView.setTextColor(Color.WHITE)
                }
                
                // Set gender spinner selection
                val genderValue = extendedInfo.gender
                if (genderValue.isNotEmpty()) {
                    val genderPosition = genderOptions.indexOf(genderValue)
                    if (genderPosition != -1) {
                        genderSpinner.setSelection(genderPosition)
                    }
                }
                
                emailEditText.setText(extendedInfo.email)
                addressEditText.setText(extendedInfo.address)
                phoneEditText.setText(extendedInfo.phone)
            }
        }
    }
    
    private fun setupButtonListeners() {
        // Done button functionality
        doneButton.setOnClickListener {
            saveUserProfile()
        }
        
        // Back button functionality
        backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun saveUserProfile() {
        val fullName = fullNameEditText.text.toString().trim()
        
        // Validate required fields
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Get current user
        val currentUser = userRepository.getUserByUsername(currentUsername!!)
        
        if (currentUser != null) {
            // Create updated user with new full name but keeping username and password the same
            val updatedUser = User(
                fullName = fullName,
                username = currentUser.username,
                password = currentUser.password
            )
            
            // Need to update user in the repository
            // Since there's no direct update method, we'll need to remove and add
            val users = userRepository.getUsers().toMutableList()
            val userIndex = users.indexOfFirst { it.username == currentUsername }
            
            if (userIndex != -1) {
                users[userIndex] = updatedUser
                userRepository.saveUsers(users)
            }
            
            // Get gender from spinner
            val selectedGender = if (genderSpinner.selectedItemPosition > 0) {
                genderSpinner.selectedItem.toString()
            } else {
                "" // Empty if "Select Gender" is chosen
            }
            
            // Create extended info object
            val extendedInfo = UserExtendedInfo(
                age = ageEditText.text.toString().trim(),
                gender = selectedGender,
                birthdate = birthdateTextView.text.toString().trim(),
                email = emailEditText.text.toString().trim(),
                address = addressEditText.text.toString().trim(),
                phone = phoneEditText.text.toString().trim()
            )
            
            // Save extended info
            sessionManager.saveUserExtendedInfo(currentUsername!!, extendedInfo)
            
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            
            // Return to profile activity
            finish()
        }
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
            navigateToActivity(SettingsActivity::class.java)
            updateBottomNavIcons(settingsIcon)
        }
    }
    
    private fun <T : AppCompatActivity> navigateToActivity(activityClass: Class<T>) {
        if (javaClass != activityClass) {
            val intent = Intent(this, activityClass)
            startActivity(intent)
            // Optional: finish this activity
            finish()
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