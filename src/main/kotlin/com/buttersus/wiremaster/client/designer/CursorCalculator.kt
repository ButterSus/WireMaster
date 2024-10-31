package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.util.delegates.RowDelegate
import com.github.yamamotoj.cachedproperty.CachedProperty
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import org.joml.Matrix3d
import org.joml.Matrix4d
import org.joml.Vector3d

@Environment(EnvType.CLIENT)
class CursorCalculator(
    private val mc: MinecraftClient,

    // Params
    unitMatrixCache: CachedProperty<Matrix3d>
) {
    // Cached fields
    private val unitMatrix: Matrix3d by unitMatrixCache
    private val forwardUnitVector: Vector3d by RowDelegate(unitMatrix, RowDelegate.FORWARD)

    // Methods
    fun calculateScrollOffset(scrollY: Double): Vector3d {
        return forwardUnitVector.mul(scrollY)
    }

    fun projectVectorOntoPlane(
        viewDirection: Vector3d,
        referenceVector: Vector3d,
        planeNormal: Vector3d = forwardUnitVector, // Optional parameter if you always use forward
    ): Vector3d {
        val projectionDepth = planeNormal.dot(referenceVector)
        val viewDepthRatio = planeNormal.dot(viewDirection)

        return Vector3d(viewDirection).mul(projectionDepth / viewDepthRatio)
    }

    fun calculateViewDirectionFromMouse(): Vector3d {
        val mousePositionNDC = convertMouseToNDC()
        return convertNDCToWorldDirection(mousePositionNDC)
    }

    private fun convertMouseToNDC(): Vector3d {
        val window = mc.window ?: throw IllegalStateException("Window is null")

        return Vector3d(
            (mc.mouse.x / window.width * 2.0 - 1.0),  // Convert x to [-1, 1]
            (mc.mouse.y / window.height * 2.0 - 1.0),  // Convert y to [-1, 1]
            1.0  // Forward direction in NDC space
        )
    }

    private fun convertNDCToWorldDirection(ndcPosition: Vector3d): Vector3d {
        val player = mc.player ?: throw IllegalStateException("Player is null")

        // Return new instance of vector (Immutability of ndcPosition is important)
        return Vector3d(ndcPosition).apply {
            // Un-project from NDC to view space
            val projectionMatrix = Matrix4d().apply {
                set(
                    mc.gameRenderer.getBasicProjectionMatrix(
                        (player.fovMultiplier * mc.options.fov.value).toDouble()
                    )
                )
                invert()
            }
            projectionMatrix.transformPosition(this)
            normalize()

            // Transform from view space to world space
            val viewRotationMatrix = Matrix4d().apply {
                rotate(mc.gameRenderer.camera.rotation)
            }
            viewRotationMatrix.transformPosition(this)

            // Negate to get the correct forward direction
            negate()
        }
    }
}