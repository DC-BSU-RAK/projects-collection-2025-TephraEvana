package com.example.fruitcolormatch

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val produceMap = mapOf(
        "Red_Fruit" to listOf(R.drawable.apple, R.drawable.strawberry),
        "Red_Vegetable" to listOf(R.drawable.tomato, R.drawable.redpepper),
        "Yellow_Fruit" to listOf(R.drawable.banana, R.drawable.lemon),
        "Yellow_Vegetable" to listOf(R.drawable.corn, R.drawable.corns),
        "Green_Fruit" to listOf(R.drawable.kiwi, R.drawable.greenapple),
        "Green_Vegetable" to listOf(R.drawable.broccoli, R.drawable.lettuce),
        "Purple_Fruit" to listOf(R.drawable.grapes, R.drawable.plum),
        "Purple_Vegetable" to listOf(R.drawable.eggplant, R.drawable.onion),
        "Orange_Fruit" to listOf(R.drawable.orange, R.drawable.mango),
        "Orange_Vegetable" to listOf(R.drawable.carrot, R.drawable.pumpkin)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showInstructionPopup()

        val spinner = findViewById<Spinner>(R.id.colorSpinner)
        val spinner2 = findViewById<Spinner>(R.id.itemSpinner)
        val button = findViewById<Button>(R.id.showFruitBtn)
        val imageView = findViewById<ImageView>(R.id.fruitImage)

        // Spinner setup
        val colors = listOf("Red", "Yellow", "Green", "Purple", "Orange")
        val types = listOf("Fruit", "Vegetable")

        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, colors)
        spinner2.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        button.setOnClickListener {
            val selectedColor = spinner.selectedItem.toString()
            val selectedType = spinner2.selectedItem.toString()

            val key = "${selectedColor}_${selectedType}"
            val produceImages = produceMap[key]

            if (produceImages != null && produceImages.isNotEmpty()) {
                val randomImage = produceImages.random()
                imageView.setImageResource(randomImage)
            } else {
                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showInstructionPopup() {
        AlertDialog.Builder(this)
            .setTitle("Welcome!")
            .setMessage("🎨 Select a color and a type (Fruit or Vegetable), then tap the button to see the matching item! 🍎🥦")
            .setPositiveButton("Let's go!") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }
}
