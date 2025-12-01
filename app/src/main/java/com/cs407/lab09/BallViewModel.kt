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

    // Scale factor to convert physical acceleration to pixel movement
    // This makes the ball move at a visually appropriate speed
    private val SCALE_FACTOR = 50f

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

                // Gravity sensor values:
                // values[0] = Gx (positive when tilted right)
                // values[1] = Gy (positive when tilted backward/up)
                // values[2] = Gz (positive when screen faces up)
                //
                // The gravity sensor measures the direction of gravity relative to the device.
                // When device is flat (screen up), Gz ≈ 9.8, Gx ≈ 0, Gy ≈ 0
                // When tilted right, Gx becomes positive (gravity pulls ball right)
                // When tilted forward (top down), Gy becomes negative (gravity pulls ball down on screen)
                //
                // Screen coordinate system: +X is right, +Y is DOWN
                // So: screen_x_acc = Gx (tilt right = ball goes right)
                //     screen_y_acc = -Gy (tilt forward = Gy negative = ball goes down = positive screen Y)

                currentBall.updatePositionAndVelocity(
                    xAcc = event.values[0] * SCALE_FACTOR,
                    yAcc = -event.values[1] * SCALE_FACTOR,
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