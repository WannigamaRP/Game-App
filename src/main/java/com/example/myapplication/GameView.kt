package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

// GameView class extending View class
class GameView(private var context: Context, private var gameTask: GameTask) : View(context) {
    private var myPaint: Paint? = null // Paint object for drawing
    private var speed = 1 // Initial speed of the game
    private var time = 0 // Time elapsed in the game
    private var score = 0 // Player's score
    private var myApplePosition = 0 // Position of the apple controlled by the player
    private val otherKnife = ArrayList<HashMap<String, Any>>() // Collection to store positions of knives thrown
    private var isGameOver = false // Flag to indicate if the game is over
    private var highScore = 0 // Highest score achieved in the game
    private var restartButton: Button? = null // Button to restart the game
    private var exitButton: Button? = null // Button to exit the game

    private var viewWidth = 0 // Width of the view
    private var viewHeight = 0 // Height of the view

    init {
        myPaint = Paint() // Initialize Paint object
    }

    // Override onDraw method to draw the game elements
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = measuredWidth // Get the measured width of the view
        viewHeight = measuredHeight // Get the measured height of the view

        // If the game is not over
        if (!isGameOver) {
            // Add a knife randomly every few milliseconds
            if (time % 700 < 10 + speed) {
                val map = HashMap<String, Any>()
                map["lane"] = (0..2).random() // Randomly select a lane for the knife
                map["startTime"] = time // Record the start time of the knife
                otherKnife.add(map) // Add the knife to the collection
            }
            time = time + 10 + speed // Increment the time elapsed in the game
            val aWidth = viewWidth / 5 // Width of the apple
            val aHeight = aWidth + 10 // Height of the apple
            myPaint!!.style = Paint.Style.FILL // Set paint style to fill
            val d = ContextCompat.getDrawable(context, R.drawable.apple) // Get drawable resource for apple

            // Draw the apple at its current position
            d?.setBounds(
                myApplePosition * viewWidth / 3 + viewWidth / 15 + 25,
                viewHeight - 2 - aHeight,
                myApplePosition * viewWidth / 3 + viewWidth / 15 + aWidth - 25,
                viewHeight - 2
            )
            d?.draw(canvas!!) // Draw the apple on the canvas

            // Draw and handle collision for each knife
            for (i in otherKnife.indices) {
                try {
                    val aX = otherKnife[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15 // X position of the knife
                    var aY = time - otherKnife[i]["startTime"] as Int // Y position of the knife
                    val d2 = ContextCompat.getDrawable(context, R.drawable.knife) // Get drawable resource for knife

                    // Draw the knife at its current position
                    d2?.setBounds(
                        aX + 25, aY - aHeight, aX + aWidth - 25, aY
                    )

                    d2?.draw(canvas) // Draw the knife on the canvas

                    // Check for collision with the apple
                    if (aY > viewHeight - 2 - aHeight && aY < viewHeight - 2) {
                        if (otherKnife[i]["lane"] as Int == myApplePosition) {
                            isGameOver = true // Game over if collision occurs
                            gameTask.closeGame(score) // Notify the game task about game closure
                        }
                    }
                    // Remove the knife if it goes beyond the view height
                    if (aY > viewHeight + aHeight) {
                        otherKnife.removeAt(i)
                        score++ // Increment score
                        speed = 1 + score / 8 // Increase speed based on score
                        if (score > highScore) {
                            highScore = score // Update high score if necessary
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            myPaint!!.color = Color.WHITE // Set paint color to white
            myPaint!!.textSize = 40f // Set text size
            canvas.drawText("Score : $score", 80f, 80f, myPaint!!) // Draw current score
            canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!) // Draw current speed
            invalidate() // Request redraw of the view
        } else {
            // Game over, show the score and start button
            myPaint!!.color = Color.WHITE
            myPaint!!.textSize = 60f
            canvas.drawText("Game Over", 200f, 300f, myPaint!!)
            canvas.drawText("Score : $score", 200f, 400f, myPaint!!)

            // Create and configure restart button
            restartButton = Button(context)
            restartButton?.text = "Start Again"
            restartButton?.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            restartButton?.setOnClickListener {
                resetGame() // Reset the game when restart button is clicked
            }
            val restartLayoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            restartLayoutParams.gravity = android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.START
            restartButton?.layoutParams = restartLayoutParams
            // Add restart button to the view
            (context as MainActivity).findViewById<FrameLayout>(android.R.id.content).addView(
                restartButton
            )

            // Create and configure exit button
            exitButton = Button(context)
            exitButton?.text = "Exit"
            exitButton?.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            exitButton?.setOnClickListener {
                gameTask.closeGame(score) // Close the game when exit button is clicked
            }
            val exitLayoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            exitLayoutParams.gravity = android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.END
            exitButton?.layoutParams = exitLayoutParams
            // Add exit button to the view
            (context as MainActivity).findViewById<FrameLayout>(android.R.id.content).addView(
                exitButton
            )
        }
    }

    // Override onTouchEvent method to handle touch events
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myApplePosition > 0) {
                        myApplePosition-- // Move apple left
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myApplePosition < 2) {
                        myApplePosition++ // Move apple right
                    }
                }
                invalidate() // Request redraw of the view
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

    // Function to reset the game
    private fun resetGame() {
        // Reset game variables
        score = 0
        speed = 1
        time = 0
        isGameOver = false
        myApplePosition = 0
        otherKnife.clear()

        // Remove the restart button and exit button from the view
        (context as MainActivity).findViewById<FrameLayout>(android.R.id.content).removeView(
            restartButton
        )
        (context as MainActivity).findViewById<FrameLayout>(android.R.id.content).removeView(
            exitButton
        )

        // Redraw the view
        invalidate()
    }
}
