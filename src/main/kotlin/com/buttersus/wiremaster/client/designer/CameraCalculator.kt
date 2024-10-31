package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.util.VectorMathUtils
import com.buttersus.wiremaster.util.atomic.ObservableAtomicReferenceWrapper
import com.github.yamamotoj.cachedproperty.CachedProperty
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathConstants.RADIANS_PER_DEGREE
import org.joml.Matrix3d
import org.joml.Quaterniond
import org.joml.Vector3d

@Suppress("unused")
class CameraCalculator(
    private val mc: MinecraftClient,

    // Fields(
    private val position: Vector3d,
    private val velocity: Vector3d,
    private val movementMatrix: Matrix3d,

    // Params
    atomicYaw: ObservableAtomicReferenceWrapper<Float>,
    atomicPitch: ObservableAtomicReferenceWrapper<Float>,

    rotationCache: CachedProperty<Quaterniond>,
    unitMatrixCache: CachedProperty<Matrix3d>,
) {
    // Fields
    private val yaw: Float by atomicYaw
    private val pitch: Float by atomicPitch

    // Cached fields
    private val rotation: Quaterniond by rotationCache
    private val unitMatrix: Matrix3d by unitMatrixCache

    // Constants
    companion object {
        private const val INITIAL_CAMERA_DISTANCE = -2.0  // Camera is in back of player
        private const val INITIAL_CAMERA_SIN = -0.5  // Since distance is negative
    }

    // Methods
    fun calculateInitialPosition(entity: Entity): Vector3d {
        return VectorMathUtils
            .toVector3d(entity.getCameraPosVec(mc.lastFrameDuration))
            .add(
                VectorMathUtils.calculateForwardUnitVectorWithHeight(
                    entity.getYaw(mc.lastFrameDuration),
                    INITIAL_CAMERA_SIN
                ).mul(INITIAL_CAMERA_DISTANCE)
            )
    }

    fun calculateInitialYawPitch(entity: Entity): Pair<Float, Float> {
        // TODO: add option to set yaw/pitch to look at player (as F5)
        return entity.getYaw(mc.lastFrameDuration) to 30.0f
    }

    fun calculateWorldInputUnitVector(): Vector3d {
        val inputVector = Designer.inputCalculator.calculateInputUnitVector()
        if (!Designer.cursorController.isActive()) return movementMatrix.transform(inputVector)
        return unitMatrix.transform(inputVector.apply {
            val temp = y
            y = z
            z = temp
        })
    }

    fun calculateTurnFactor(worldInputVector: Vector3d): Double {
        val angle = velocity.angleCos(worldInputVector)
        return when {
            angle > RADIANS_PER_DEGREE * 120 -> 0.8
            angle > RADIANS_PER_DEGREE * 60 -> 0.9
            else -> 0.95
        }
    }

    fun isMoving(): Boolean {
        return velocity.lengthSquared() > 0
    }
}