package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    // Expose the ball's position as a StateFlow
    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    /**
     * Called by the UI when the game field's size is known.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            ball = Ball(fieldWidth, fieldHeight, ballSizePx)
            _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
        }
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        // Ensure ball is initialized
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            if (lastTimestamp != 0L) {
                // Calculate time difference in seconds
                val NS2S = 1.0f / 1000000000.0f
                val dT = (event.timestamp - lastTimestamp) * NS2S

                // Update ball position and velocity
                // Note: Gravity sensor reports opposite of actual gravity
                // Also note: sensor y-axis points up, screen y-axis points down
                currentBall.updatePositionAndVelocity(
                    xAcc = -event.values[0],  // Negate because sensor reports opposite
                    yAcc = event.values[1],   // Positive because screen y is inverted
                    dT = dT
                )

                // Update StateFlow to notify UI
                _ballPosition.update { Offset(currentBall.posX, currentBall.posY) }
            }

            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        ball?.reset()
        ball?.let {
            _ballPosition.value = Offset(it.posX, it.posY)
        }
        lastTimestamp = 0L
    }
}