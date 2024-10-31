package com.buttersus.wiremaster.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.math.MathConstants.RADIANS_PER_DEGREE
import net.minecraft.util.math.Vec3d
import org.joml.Matrix3d
import org.joml.Quaterniond
import org.joml.Vector3d
import kotlin.math.cos
import kotlin.math.sin

@Environment(EnvType.CLIENT)
object VectorMathUtils {
    // Cached computation methods
    fun calculateRotation(yaw: Float, pitch: Float): Quaterniond {
        return Quaterniond().rotationYXZ(
            (-yaw * RADIANS_PER_DEGREE).toDouble(),
            (pitch * RADIANS_PER_DEGREE).toDouble(),
            0.0
        )
    }

    fun calculateUnitMatrix(rotation: Quaterniond): Matrix3d {
        val matrix = Matrix3d()
        rotation.get(matrix)
        return matrix
    }

    // Math methods
    fun calculateForwardUnitVectorWithHeight(yaw: Float, height: Double = 0.0): Vector3d {
        return Vector3d(
            -sin(yaw * RADIANS_PER_DEGREE).toDouble(),
            height,
            cos(yaw * RADIANS_PER_DEGREE).toDouble()
        )
    }

    // Type cast methods
    @JvmStatic
    fun toVector3d(vec3d: Vec3d): Vector3d = Vector3d(vec3d.x, vec3d.y, vec3d.z)

    @JvmStatic
    fun toVec3d(vector3d: Vector3d): Vec3d = Vec3d(vector3d.x, vector3d.y, vector3d.z)
}
