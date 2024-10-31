package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.util.VectorMathUtils
import com.buttersus.wiremaster.util.atomic.ObservableAtomicReferenceWrapper
import com.buttersus.wiremaster.util.delegates.RowDelegate
import com.github.yamamotoj.cachedproperty.CachedProperty
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.joml.Matrix3d
import org.joml.Quaterniond

@Environment(EnvType.CLIENT)
class CameraMovementMatrixController(
    movementMatrix: Matrix3d,
    atomicYaw: ObservableAtomicReferenceWrapper<Float>,
    atomicPitch: ObservableAtomicReferenceWrapper<Float>,

    rotationCache: CachedProperty<Quaterniond>
) {
    // Fields
    private var yaw by atomicYaw
    private var pitch by atomicPitch

    // Cached fields
    private val rotation by rotationCache

    // We can't observe global variables in WireMaster
    // So we have to rebuild the movement matrix on every update
    private var forwardMovementVector by RowDelegate(movementMatrix, RowDelegate.FORWARD)
    private var rightMovementVector by RowDelegate(movementMatrix, RowDelegate.RIGHT)
    private var upMovementVector by RowDelegate(movementMatrix, RowDelegate.UP)

    // Customizable logic
    fun rebuildMovementMatrix() {  // This is not cache since it requires manual rebuild
        println("Rebuilding movement matrix")

        forwardMovementVector.apply {
            if (WireMaster.MOVEMENT_TYPE == CameraMovementType.FLAT) {
                set(VectorMathUtils.calculateForwardUnitVectorWithHeight(yaw))
            } else {
                set(0.0, 0.0, 1.0).rotate(rotation)
            }
        }

        upMovementVector.apply {
            if (WireMaster.MOVEMENT_TYPE == CameraMovementType.ABSOLUTE) {
                set(0.0, 1.0, 0.0).rotate(rotation)
            } else {
                set(0.0, 1.0, 0.0)
            }
        }

        rightMovementVector.apply {
            set(1.0, 0.0, 0.0).rotate(rotation)
        }
    }
}