package com.buttersus.wiremaster.client.camera.interpolation

import org.joml.Vector3d

abstract class DampInterpolation(
    private val posReference: Vector3d
) {
    protected val velocity: Vector3d = Vector3d(0.0, 0.0, 0.0)
    val displacement: Vector3d = Vector3d(0.0, 0.0, 0.0)

    // Update method to be called every frame
    abstract fun update(frameTime: Double)

    protected fun move(delta: Vector3d) {
        posReference.add(delta)
        displacement.sub(delta)
    }

    // Displacement getters & setters
    fun reset() {
        displacement.set(0.0, 0.0, 0.0)
    }

    fun set(displacement: Vector3d) {
        this.displacement.set(displacement)
    }

    fun add(displacement: Vector3d) {
        this.displacement.add(displacement)
    }

    fun get() = displacement
}