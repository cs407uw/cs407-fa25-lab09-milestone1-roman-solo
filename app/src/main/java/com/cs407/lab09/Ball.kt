package com.cs407.lab09

/**
 * Represents a ball that can move. (No Android UI imports!)
 *
 * Constructor parameters:
 * - backgroundWidth: the width of the background, of type Float
 * - backgroundHeight: the height of the background, of type Float
 * - ballSize: the width/height of the ball, of type Float
 */
class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    var posX = 0f
    var posY = 0f
    var velocityX = 0f
    var velocityY = 0f
    private var accX = 0f
    private var accY = 0f

    private var isFirstUpdate = true

    init {
        reset()
    }

    /**
     * Updates the ball's position and velocity based on the given acceleration and time step.
     * (See lab handout for physics equations)
     */
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        if(isFirstUpdate) {
            isFirstUpdate = false
            accX = xAcc
            accY = yAcc
            return
        }

        // Store previous acceleration
        val prevAccX = accX
        val prevAccY = accY

        // Update acceleration
        accX = xAcc
        accY = yAcc

        // Calculate distance traveled using equation (2) from lab handout
        // l = v0 * dT + (1/6) * dT^2 * (3*a0 + a1)
        val deltaX = velocityX * dT + (1f / 6f) * dT * dT * (3f * prevAccX + accX)
        val deltaY = velocityY * dT + (1f / 6f) * dT * dT * (3f * prevAccY + accY)

        // Update position
        posX += deltaX
        posY += deltaY

        // Calculate new velocity using equation (1) from lab handout
        // v1 = v0 + (1/2) * (a0 + a1) * dT
        velocityX = velocityX + 0.5f * (prevAccX + accX) * dT
        velocityY = velocityY + 0.5f * (prevAccY + accY) * dT

        // Check boundaries after updating position
        checkBoundaries()
    }

    /**
     * Ensures the ball does not move outside the boundaries.
     * When it collides, velocity and acceleration perpendicular to the
     * boundary should be set to 0.
     */
    fun checkBoundaries() {
        // Check left boundary (x = 0)
        if (posX < 0) {
            posX = 0f
            velocityX = 0f
            accX = 0f
        }

        // Check right boundary (x = backgroundWidth - ballSize)
        if (posX > backgroundWidth - ballSize) {
            posX = backgroundWidth - ballSize
            velocityX = 0f
            accX = 0f
        }

        // Check top boundary (y = 0)
        if (posY < 0) {
            posY = 0f
            velocityY = 0f
            accY = 0f
        }

        // Check bottom boundary (y = backgroundHeight - ballSize)
        if (posY > backgroundHeight - ballSize) {
            posY = backgroundHeight - ballSize
            velocityY = 0f
            accY = 0f
        }
    }

    /**
     * Resets the ball to the center of the screen with zero
     * velocity and acceleration.
     */
    fun reset() {
        // Reset position to center of screen
        posX = (backgroundWidth - ballSize) / 2f
        posY = (backgroundHeight - ballSize) / 2f

        // Reset velocity to zero
        velocityX = 0f
        velocityY = 0f

        // Reset acceleration to zero
        accX = 0f
        accY = 0f

        // Reset first update flag
        isFirstUpdate = true
    }
}