package com.speed.swifttest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import androidx.constraintlayout.widget.ConstraintLayout



class MainActivity : AppCompatActivity() {

    private lateinit var testNameText: TextView
    private lateinit var speedText: TextView
    private lateinit var downloadResultText: TextView
    private lateinit var uploadResultText: TextView
    private lateinit var arc1: ImageView
    private lateinit var arc2: ImageView
    private lateinit var arc3: ImageView
    private lateinit var arc4: ImageView
    private lateinit var tapToStartText: TextView
    private lateinit var rotateAnimation: RotateAnimation
    private val handler = Handler(Looper.getMainLooper())
    private var isTestRunning = false
    private var testCancelled = false

    // Bottom navigation views
    private lateinit var homeIcon: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var infoIcon: ImageView
    private lateinit var settingsIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize views
        testNameText = findViewById(R.id.test_name_text)
        speedText = findViewById(R.id.speed_text)
        downloadResultText = findViewById(R.id.download_result_text)
        uploadResultText = findViewById(R.id.upload_result_text)


        // Initialize arc views
        arc1 = findViewById(R.id.arc_1)
        arc2 = findViewById(R.id.arc_2)
        arc3 = findViewById(R.id.arc_3)
        arc4 = findViewById(R.id.arc_4)

        // Initialize bottom navigation views
        homeIcon = findViewById(R.id.home_icon)
        profileIcon = findViewById(R.id.profile_icon)
        infoIcon = findViewById(R.id.info_icon)
        settingsIcon = findViewById(R.id.settings_icon)

        // Initialize animation view
        tapToStartText = findViewById(R.id.tap_to_start_text)
        startBreathingAnimation(tapToStartText)

        // Set up bottom navigation click listeners
        setupBottomNavigation()

        // Enable automatic text size adjustment for Android API 26+
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                testNameText,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                speedText,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
        }

        // Set up animation for the arc progress
        setupRotateAnimation()

        // Set up click listener for the start test
        val parentLayout =
            findViewById<ConstraintLayout>(R.id.parent_layout)
        parentLayout.setOnClickListener {
            if (isNetworkAvailable()) {
                if (!isTestRunning) {
                    tapToStartText.visibility = View.GONE
                    startTest() // Start the test if it's not already running
                } else {
                    // Cancel the test if already running
                    testCancelled = true
                }
            } else {
                showNoInternetDialog() // Show dialog if no internet
            }
        }
    }

    private fun startBreathingAnimation(view: View) {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f)
        ).apply {
            duration = 600
        }

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f, 1f)
        ).apply {
            duration = 600
        }
        val animatorSet = AnimatorSet().apply {
            playSequentially(scaleUp, scaleDown)
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Restart animation for infinite loop
                    start()
                }
            })
        }
        animatorSet.start()
    }

    private fun setupBottomNavigation() {
       homeIcon.setOnClickListener {
            // Already on home, no need to navigate
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

    private fun updateBottomNavIcons(selectedIcon: ImageView) {
        // Reset all icons to gray
        homeIcon.setColorFilter(getColor(android.R.color.darker_gray))
        profileIcon.setColorFilter(getColor(android.R.color.darker_gray))
        infoIcon.setColorFilter(getColor(android.R.color.darker_gray))
        settingsIcon.setColorFilter(getColor(android.R.color.darker_gray))

        // Set selected icon to black
        selectedIcon.setColorFilter(getColor(android.R.color.black))
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        if (!isTestRunning) {
            val intent = Intent(this, activityClass)
            startActivity(intent)
        } else {
            // Show a dialog asking if the user wants to cancel the test and navigate
            AlertDialog.Builder(this)
                .setTitle("Test in Progress")
                .setMessage("Do you want to cancel the test and navigate?")
                .setPositiveButton("Yes") { _, _ ->
                    testCancelled = true
                    resetUI()
                    val intent = Intent(this, activityClass)
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupRotateAnimation() {
        // Create and apply animations to individual arcs
        rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 3000 // 3 seconds for a full rotation
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
        }
    }

    private fun startArcAnimations() {
        // Create animations with different durations and directions
        createRotationAnimation(arc1, 3000f, true)  // 3 seconds, clockwise
        createRotationAnimation(arc2, 4000f, false) // 4 seconds, counter-clockwise
        createRotationAnimation(arc3, 5000f, true)  // 5 seconds, clockwise
        createRotationAnimation(arc4, 6000f, false) // 6 seconds, counter-clockwise
    }

    private fun createRotationAnimation(view: ImageView, duration: Float, clockwise: Boolean) {
        val endDegrees = if (clockwise) 360f else -360f

        val rotate = RotateAnimation(
            0f, endDegrees,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            this.duration = duration.toLong()
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
        }

        view.startAnimation(rotate)
    }

    private fun startTest() {

        downloadResultText.visibility = View.VISIBLE
        uploadResultText.visibility = View.VISIBLE

        // Change UI to testing state
        isTestRunning = true
        testCancelled = false

        // Set test name text with auto-sizing in mind
        testNameText.text = "Testing Download..."

        // Initially show speed as 0.00 and make visible
        speedText.text = "0.00 Mbps"
        speedText.visibility = View.VISIBLE

        // Start the arc rotation animations
        startArcAnimations()

        CoroutineScope(Dispatchers.IO).launch {
            val downloadSpeed = measureDownloadSpeedWithRealtime()

            if (!testCancelled) {
                withContext(Dispatchers.Main) {
                    // Save download result
                    val formattedDownloadSpeed = (downloadSpeed * 100).roundToInt() / 100.0
                    downloadResultText.text = "Download: \n$formattedDownloadSpeed Mbps"
                    downloadResultText.visibility = View.VISIBLE

                    // Update to upload test
                    testNameText.text = "Testing Upload..."
                    speedText.text = "0.00 Mbps"

                    CoroutineScope(Dispatchers.IO).launch {
                        val uploadSpeed = measureUploadSpeedWithRealtime()

                        withContext(Dispatchers.Main) {
                            if (!testCancelled) {
                                // Display final results
                                val formattedUploadSpeed = (uploadSpeed * 100).roundToInt() / 100.0
                                uploadResultText.text = "Upload: \n$formattedUploadSpeed Mbps"
                                uploadResultText.visibility = View.VISIBLE

                                // Update test name to show completion
                                testNameText.text = "Test Complete"

                                startBreathingAnimation(tapToStartText)

                            }
                            resetUI(keepResults = !testCancelled)
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    resetUI(keepResults = false)
                }
            }
        }
    }

    private fun resetUI(keepResults: Boolean = false) {
        isTestRunning = false
        testCancelled = false

        if (!keepResults) {
            testNameText.text = "SwifTest"
            speedText.visibility = View.GONE
            downloadResultText.visibility = View.GONE
            uploadResultText.visibility = View.GONE
        } else {
            speedText.visibility = View.GONE
        }

        // Clear animations from all arcs
        arc1.clearAnimation()
        arc2.clearAnimation()
        arc3.clearAnimation()
        arc4.clearAnimation()

        // Show and restart the "Tap to Start" animation
        tapToStartText.visibility = View.VISIBLE
        startBreathingAnimation(tapToStartText) // Restart the animation
    }

    private suspend fun measureDownloadSpeedWithRealtime(): Double {
        val testUrl = "https://speed.cloudflare.com/__down?bytes=25000000"
        return try {
            val startTime = System.nanoTime()
            val connection = URL(testUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()

            val inputStream: InputStream = connection.inputStream
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0
            var lastUpdateTime = startTime
            var lastUpdateBytes = 0

            while (inputStream.read(buffer).also { bytesRead = it } != -1 && !testCancelled) {
                totalBytesRead += bytesRead

                // Update speed every 200ms
                val currentTime = System.nanoTime()
                val timeSinceLastUpdate = currentTime - lastUpdateTime

                if (timeSinceLastUpdate > 200_000_000) { // 200ms in nanoseconds
                    val bytesReadSinceLastUpdate = totalBytesRead - lastUpdateBytes
                    val instantSpeed = calculateSpeedMbps(bytesReadSinceLastUpdate, timeSinceLastUpdate)

                    // Update UI with current speed
                    withContext(Dispatchers.Main) {
                        updateSpeedDisplay(instantSpeed)
                    }

                    lastUpdateTime = currentTime
                    lastUpdateBytes = totalBytesRead
                }
            }

            inputStream.close()
            connection.disconnect()

            if (testCancelled) return 0.0

            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1_000_000_000.0 // convert to seconds

            val downloadSpeed = (totalBytesRead * 8.0) / (1024 * 1024 * duration) // Mbps
            downloadSpeed
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    private suspend fun measureUploadSpeedWithRealtime(): Double {
        // This implementation uses a separate monitoring coroutine to update the UI
        // while the upload is happening
        val testUrl = "https://speed.cloudflare.com/__up"

        try {
            val dataSize = 10000000 // 10MB of data to upload
            val dataToUpload = ByteArray(dataSize)
            val startTime = System.nanoTime()

            var totalUploaded = 0
            var isUploading = true

            // Start a monitoring coroutine to track and display progress
            val monitorJob = CoroutineScope(Dispatchers.IO).launch {
                var lastTime = System.nanoTime()
                var lastBytes = 0

                while (isUploading && !testCancelled) {
                    delay(200) // Update every 200ms

                    val currentTime = System.nanoTime()
                    val bytesDelta = totalUploaded - lastBytes
                    val timeDelta = currentTime - lastTime

                    if (timeDelta > 0 && bytesDelta > 0) {
                        val instantSpeed = calculateSpeedMbps(bytesDelta, timeDelta)

                        withContext(Dispatchers.Main) {
                            updateSpeedDisplay(instantSpeed)
                        }

                        lastTime = currentTime
                        lastBytes = totalUploaded
                    }
                }
            }

            // The actual upload process
            val connection = URL(testUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            // Set up output stream to measure upload speed
            val outputStream = connection.outputStream
            val chunkSize = 64 * 1024 // 64KB chunks

            while (totalUploaded < dataSize && !testCancelled) {
                val bytesToWrite = minOf(chunkSize, dataSize - totalUploaded)
                outputStream.write(dataToUpload, totalUploaded, bytesToWrite)
                outputStream.flush()
                totalUploaded += bytesToWrite

                // Small delay to allow monitoring coroutine to update
                delay(5)
            }

            outputStream.close()

            // Only get response if we completed the upload
            if (!testCancelled) {
                val responseCode = connection.responseCode
            }

            connection.disconnect()

            // Stop the monitoring
            isUploading = false
            monitorJob.join() // Wait for monitor to finish

            if (testCancelled) return 0.0

            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1_000_000_000.0 // convert to seconds

            val uploadSpeed = (totalUploaded * 8.0) / (1024 * 1024 * duration) // Mbps
            return uploadSpeed

        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }

    private fun calculateSpeedMbps(bytes: Int, nanoseconds: Long): Double {
        val seconds = nanoseconds / 1_000_000_000.0
        // Convert bytes to bits, then to megabits
        return (bytes * 8.0) / (1024 * 1024 * seconds)
    }

    private fun updateSpeedDisplay(speedMbps: Double) {
        // Format the speed with proper units and apply to TextView
        val speedText = when {
            speedMbps < 1.0 -> {
                val speedKbps = speedMbps * 1000
                val formattedSpeed = (speedKbps * 100).roundToInt() / 100.0
                "$formattedSpeed Kbps"
            }
            else -> {
                val formattedSpeed = (speedMbps * 100).roundToInt() / 100.0
                "$formattedSpeed Mbps"
            }
        }

        // Update UI on main thread
        runOnUiThread {
            this.speedText.text = speedText
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}