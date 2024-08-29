package com.buttersus.wiremaster.client.camera.interpolation

import org.joml.Vector3d
import kotlin.math.exp

@Suppress("unused")
class ExponentialInterpolation(
    posReference: Vector3d,
    private val smoothingFactor: Double,
    private val responseFactor: Double
) : DampInterpolation(posReference) {
    override fun update(frameTime: Double) {
        // Calculate the factor to apply for the current frame based on the smoothing factor
        val factor = exp(-smoothingFactor * frameTime * responseFactor)

        // Update velocity: apply the smoothing factor to the current velocity
        // and include the current displacement for dynamic input adjustments
        velocity.lerp(displacement, 1.0 - factor)

        // Compute the change based on the updated velocity
        val change = Vector3d(velocity).mul(frameTime * responseFactor)

        // Move the reference position by the computed change
        move(change)
    }

    companion object {
        fun createBasic(posReference: Vector3d) = ExponentialInterpolation(posReference, 5.0, 5.0)
        fun createQuick(posReference: Vector3d) = ExponentialInterpolation(posReference, 2.5, 25.0)
    }
}

