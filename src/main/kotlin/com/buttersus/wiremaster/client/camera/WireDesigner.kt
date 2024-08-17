package com.buttersus.wiremaster.client.camera

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.extensions.clamp
import com.buttersus.wiremaster.extensions.toDouble
import com.buttersus.wiremaster.extensions.toRadians
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import org.joml.Quaterniond
import org.joml.Vector3d
import org.lwjgl.glfw.GLFW
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Environment(EnvType.CLIENT)
object WireDesigner {
    private val mc = MinecraftClient.getInstance()
    private val rotation = Quaterniond(0.0, 0.0, 0.0, 1.0)
    private val forwards = Vector3d(0.0, 0.0, 1.0)
    private val up = Vector3d(0.0, 1.0, 0.0)
    private val left = Vector3d(1.0, 0.0, 0.0)
    private var active = false
    private var wasInCursorMode = false
    private var cursorMode = false
    private var oldPerspective: Perspective? = null
    private var oldInput: Input? = null
    private var cameraInput: Input? = null
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0
    private var yaw = 0.0
    private var pitch = 0.0
    private var forwardVelocity = 0.0
    private var leftVelocity = 0.0
    private var upVelocity = 0.0
    private var lastTime = 0L
    private var gameRendererPicking = false
    private var cameraMovementType = CameraMovementType.NORMAL

    fun init() {
        ClientTickEvents.START_CLIENT_TICK.register {
            onClientTickStart()
        }
    }

    // Wire Designer
    fun toggleWireDesigner(): Boolean = when (active) {
        false -> enableWireDesigner()
        true -> disableWireDesigner()
    }

    private fun disableWireDesigner(): Boolean {
        if (!active) return false
        val player = mc.player ?: return false
        active = false
        if (cursorMode) disableCursorMode()

        val cameraType = mc.options.perspective
        mc.options.perspective = oldPerspective
        player.input = oldInput
        if (cameraType.isFirstPerson != mc.options.perspective.isFirstPerson)
            mc.gameRenderer.onCameraEntitySet(if (mc.options.perspective.isFirstPerson) mc.getCameraEntity() else null)
        oldPerspective = null
        return true
    }

    private fun enableWireDesigner(): Boolean {
        if (active) return false
        val player = mc.player ?: return false
        val entity = mc.getCameraEntity() ?: return false
        active = true

        // Store old values
        oldPerspective = mc.options.perspective
        oldInput = player.input

        // Set new values
        cameraInput = Input()
        player.input = cameraInput
        mc.options.perspective = Perspective.THIRD_PERSON_BACK
        if (oldPerspective?.isFirstPerson != mc.options.perspective.isFirstPerson)
            mc.gameRenderer.onCameraEntitySet(if (mc.options.perspective.isFirstPerson) mc.getCameraEntity() else null)

        val frameTime = mc.lastFrameDuration
        val pos = entity.getCameraPosVec(frameTime)
        x = pos.x
        y = pos.y
        z = pos.z
        yaw = entity.getYaw(frameTime).toDouble()
        pitch = entity.getPitch(frameTime).toDouble()

        calculateVectors()

        val distance = -2.0
        x += forwards.x * distance
        y += forwards.y * distance
        z += forwards.z * distance

        forwardVelocity = 0.0
        leftVelocity = 0.0
        upVelocity = 0.0
        lastTime = 0L
        return true
    }

    // Cursor mode
    fun toggleCursorMode(): Boolean {
        if (mc.window == null) return false
        return when (cursorMode) {
            false -> enableCursorMode()
            true -> disableCursorMode()
        }
    }

    private fun enableCursorMode(): Boolean {
        if (mc.window == null) return false
        cursorMode = true
        GLFW.glfwSetInputMode(mc.window.handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
        return true
    }

    private fun disableCursorMode(): Boolean {
        if (mc.window == null) return false
        cursorMode = false
        GLFW.glfwSetInputMode(mc.window.handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
        return true
    }

    fun onScreenOpen() {
        if (cursorMode) {
            wasInCursorMode = true
            disableCursorMode()
        }
    }

    fun onScreenClose() {
        if (wasInCursorMode) {
            wasInCursorMode = false
            enableCursorMode()
        }
    }

    // Math
    private fun calculateVectors() {
        rotation.rotationYXZ(-yaw * (PI.toFloat() / 180.0), pitch * (PI.toFloat() / 180.0), 0.0)
        forwards.set(
            if (cameraMovementType == CameraMovementType.FLAT)
                Vector3d(-sin(yaw.toRadians()), 0.0, cos(yaw.toRadians()))
            else
                Vector3d(0.0, 0.0, 1.0).rotate(rotation)
        )
        up.set(
            if (cameraMovementType == CameraMovementType.ABSOLUTE)
                Vector3d(0.0, 1.0, 0.0).rotate(rotation)
            else
                Vector3d(0.0, 1.0, 0.0)
        )
        left.set(1.0, 0.0, 0.0).rotate(rotation)
    }

    @Suppress("LocalVariableName")
    private fun combineMovement(
        _velocity: Double, impulse: Double, frameTime: Double,
        acceleration: Double, slowdown: Double
    ): Double {
        var velocity = _velocity
        // When moving opposite direction, velocity resets
        if (impulse != 0.0) {
            if (impulse > 0 && velocity < 0 || impulse < 0 && velocity > 0) {
                if (WireMaster.COUNTER_STRAFING) velocity = 0.0
                else velocity *= slowdown.pow(2)
            }
            velocity += acceleration * impulse * frameTime
        } else {
            velocity *= slowdown
        }
        return velocity
    }

    // Events && Mixin Methods
    fun onPlayerTurn(player: ClientPlayerEntity, yRot: Double, xRot: Double) {
        if (active) {
            if (!cursorMode) {
                pitch += xRot.toFloat() * 0.15f
                yaw += yRot.toFloat() * 0.15f
                pitch = MathHelper.clamp(pitch, -90.0, 90.0)  // Player can't turn 180 degrees vertically
            }
            calculateVectors()
        } else player.changeLookDirection(yRot, xRot)
    }

    fun onWorldUnload() {
        disableWireDesigner()
        cursorMode = false
        wasInCursorMode = false
    }

    fun onRenderCrosshairIsFirstPerson(cameraType: Perspective) = active || cameraType.isFirstPerson
    fun onRenderItemInHandIsFirstPerson(cameraType: Perspective) = active || cameraType.isFirstPerson
    fun onBeforeGameRendererPick() {
        gameRendererPicking = true
    }

    fun onAfterGameRendererPick() {
        gameRendererPicking = false
    }

    fun shouldOverrideCameraEntityPosition(entity: Entity) =
        active && (entity == mc.getCameraEntity() && gameRendererPicking)

    fun onClientTickStart() {
        if (!active) return
        val togglePerspectiveKey = mc.options.togglePerspectiveKey
        while (togglePerspectiveKey.wasPressed()) continue
        togglePerspectiveKey.isPressed = false
        oldInput?.tick(false, 0f)
    }

    fun onRenderTickStart() {
        if (!active) return
        if (lastTime == 0L) return Unit.also { lastTime = System.nanoTime() }
        val currentTime = System.nanoTime()
        val frameTime = (currentTime - lastTime) / 1e9
        lastTime = currentTime

        val input = oldInput ?: return
        val forwardImpulse = input.pressingForward.toDouble() - input.pressingBack.toDouble()
        val leftImpulse = input.pressingLeft.toDouble() - input.pressingRight.toDouble()
        val upImpulse = input.jumping.toDouble() - input.sneaking.toDouble()
        val slowdown = WireMaster.SLOWDOWN.pow(frameTime)

        forwardVelocity = combineMovement(forwardVelocity, forwardImpulse, frameTime, WireMaster.ACCELERATION, slowdown)
        leftVelocity = combineMovement(leftVelocity, leftImpulse, frameTime, WireMaster.ACCELERATION, slowdown)
        upVelocity = combineMovement(upVelocity, upImpulse, frameTime, WireMaster.ACCELERATION, slowdown)

        // Getting radius-vector
        val velocity = Vector3d()
            .add(Vector3d(forwards).mul(forwardVelocity))
            .add(Vector3d(left).mul(leftVelocity))
            .add(Vector3d(up).mul(upVelocity))
            .clamp(0.0, WireMaster.MAX_SPEED)

        x += velocity.x * frameTime
        y += velocity.y * frameTime
        z += velocity.z * frameTime
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMouseScroll(mouseX: Double, mouseY: Double, scrollY: Double): Boolean {
        val scrollAmount = scrollY * mc.options.mouseSensitivity.value
        println("Scroll amount: $scrollAmount")
        return true
    }

    // Getters
    fun isActive() = active
    fun getXRot() = pitch
    fun getYRot() = yaw
    fun getX() = x
    fun getY() = y
    fun getZ() = z

    // Other methods
    fun canToggle() = mc.currentScreen == null && mc.world != null && mc.player != null  // Not in a GUI
    fun canToggleCursorMode() =
        active && mc.currentScreen == null && mc.world != null && mc.player != null  // Active & not in a GUI

    fun canScroll() =
        active && mc.currentScreen == null && mc.world != null && mc.player != null  // Active & not in a GUI
}
