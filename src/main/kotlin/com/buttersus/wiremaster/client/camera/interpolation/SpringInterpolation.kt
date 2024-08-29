package com.buttersus.wiremaster.client.camera.interpolation

import org.joml.Vector3d

class SpringInterpolation(
    posReference: Vector3d,
    private val stiffness: Double,
    private val mass: Double,
    private val damping: Double
) : DampInterpolation(posReference) {
    override fun update(frameTime: Double) {
        // Calculate spring force
        val springForce = Vector3d(displacement).mul(stiffness)

        // Calculate damping force
        val dampingForce = Vector3d(velocity).mul(damping)

        // Calculate total force
        val totalForce = Vector3d(springForce).sub(dampingForce)

        // Calculate acceleration
        val acceleration = Vector3d(totalForce).div(mass)

        // Update velocity
        velocity.add(Vector3d(acceleration).mul(frameTime))

        // Calculate displacement change
        val displacementChange = Vector3d(velocity).mul(frameTime)

        // Move the reference position
        move(displacementChange)
    }

    companion object {
        fun createBasic(posReference: Vector3d) = SpringInterpolation(posReference, 80.0, 1.0, 18.0)
    }
}
