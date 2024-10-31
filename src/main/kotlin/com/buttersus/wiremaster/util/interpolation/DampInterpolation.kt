package com.buttersus.wiremaster.util.interpolation

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.joml.Vector3d

@Environment(EnvType.CLIENT)
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
    fun resetVector() {
        displacement.set(0.0, 0.0, 0.0)
    }

    fun setVector(displacement: Vector3d) {
        this.displacement.set(displacement)
    }

    fun addVector(displacement: Vector3d) {
        this.displacement.add(displacement)
    }

    fun getVector() = displacement
}