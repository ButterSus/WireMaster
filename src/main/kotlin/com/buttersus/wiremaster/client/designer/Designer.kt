package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.client.event.MinecraftClientEvents
import com.buttersus.wiremaster.client.keybinding.KeyBindingController
import com.buttersus.wiremaster.util.VectorMathUtils
import com.buttersus.wiremaster.util.atomic.ObservableAtomicReferenceWrapper
import com.buttersus.wiremaster.util.interfaces.Initializable
import com.github.yamamotoj.cachedproperty.CachedProperty
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import org.joml.Matrix3d
import org.joml.Vector3d
import java.util.concurrent.atomic.AtomicBoolean

@Environment(EnvType.CLIENT)
object Designer : Initializable, MinecraftClientEvents.WorldUnload {
    private val mc = MinecraftClient.getInstance()

    // Atomic flags
    private val atomicCameraActive = AtomicBoolean(false)
    private val atomicCursorActive = AtomicBoolean(false)
    private val atomicGameRendererPicking = AtomicBoolean(false)

    // Keys
    private val atomicCursorMovementKeyHeld = AtomicBoolean(false)
    private val atomicSprintKeyHeld = AtomicBoolean(false)

    // Cached properties
    private val cameraRotationCache =
        CachedProperty { VectorMathUtils.calculateRotation(atomicCameraYaw.get(), atomicCameraPitch.get()) }
    private val cameraRotationGetter by cameraRotationCache
    private val unitMatrixCache = CachedProperty { VectorMathUtils.calculateUnitMatrix(cameraRotationGetter) }

    // Movement vars
    private val cameraPosition = Vector3d()
    private val cameraVelocity = Vector3d()
    private val atomicCameraYaw: ObservableAtomicReferenceWrapper<Float> =
        ObservableAtomicReferenceWrapper(0.0f, cameraRotationCache::invalidate)
    private val atomicCameraPitch: ObservableAtomicReferenceWrapper<Float> =
        ObservableAtomicReferenceWrapper(0.0f, cameraRotationCache::invalidate)
    private val movementMatrix = Matrix3d()

    fun invalidateAllCaches() {
        // Caches
        cameraRotationCache.invalidate()
        unitMatrixCache.invalidate()
        cameraMovementMatrixController.rebuildMovementMatrix()
    }

    @JvmField
    val cameraController = CameraController(
        mc, atomicCameraActive, atomicGameRendererPicking, atomicSprintKeyHeld,
        cameraPosition, cameraVelocity, atomicCameraYaw, atomicCameraPitch
    )

    @JvmField
    val cameraCalculator = CameraCalculator(
        mc, cameraPosition, cameraVelocity, movementMatrix, atomicCameraYaw, atomicCameraPitch,
        cameraRotationCache, unitMatrixCache
    )

    @JvmField
    val cameraMovementMatrixController = CameraMovementMatrixController(
        movementMatrix, atomicCameraYaw, atomicCameraPitch, cameraRotationCache
    )

    @JvmField
    val cursorController = CursorController(
        mc, cameraPosition, atomicCursorActive, atomicCursorMovementKeyHeld,
        atomicCameraYaw, atomicCameraPitch
    )

    @JvmField
    val cursorCalculator = CursorCalculator(mc, unitMatrixCache)

    @JvmField
    val inputController = InputController(mc)

    @JvmField
    val inputCalculator = InputCalculator(mc)

    @JvmField
    val rendererController = RendererController(mc)

    @JvmField
    val designerStateController = DesignerStateController(atomicCameraActive, atomicCursorActive)

    @JvmField
    val interpolationManager = InterpolationManager(cameraPosition)

    // Impl: Initializable
    override fun init() {
        cameraController.init()
        cursorController.init()
        inputController.init()
        interpolationManager.init()

        // Pass keys
        KeyBindingController.CURSOR_MOVEMENT.passAtomicKeyState(atomicCursorMovementKeyHeld)
        KeyBindingController.SPRINT.passAtomicKeyState(atomicSprintKeyHeld)

        // Register events
        MinecraftClientEvents.WORLD_UNLOAD.register(this)
    }

    // Impl: Events
    override fun onWorldUnload(screen: Screen) {
        cameraController.disable()
    }
}

