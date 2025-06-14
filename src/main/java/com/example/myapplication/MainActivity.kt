package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// MainActivity class extending AppCompatActivity class and implementing GameTask interface
class MainActivity : AppCompatActivity(), GameTask {
    private lateinit var rootLayout: LinearLayout // Layout to hold the game view
    private lateinit var startBtn: Button // Button to start the game
    private lateinit var mGameView: GameView // Game view
    private lateinit var score: TextView // TextView to display score

    // Override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the layout for the activity
        startBtn = findViewById(R.id.startBtn) // Initialize startBtn with the button from the layout
        rootLayout = findViewById(R.id.rootLayout) // Initialize rootLayout with the layout from the XML file
        score = findViewById(R.id.score) // Initialize score with the TextView from the layout
        mGameView = GameView(this, this) // Initialize mGameView with a new instance of GameView

        // Set onClickListener for the start button
        startBtn.setOnClickListener {
            mGameView.setBackgroundResource(R.drawable.track) // Set background resource for the game view
            rootLayout.addView(mGameView) // Add the game view to the root layout
            startBtn.visibility = View.GONE // Hide the start button
            score.visibility = View.GONE // Hide the score TextView
        }
    }

    // Override closeGame method from GameTask interface
    override fun closeGame(mScore: Int) {
        this.score.text = "Score : 4" // Update the score TextView
        rootLayout.removeView(mGameView) // Remove the game view from the root layout
        startBtn.visibility = View.VISIBLE // Show the start button
        this.score.visibility = View.VISIBLE // Show the score TextView
    }
}
