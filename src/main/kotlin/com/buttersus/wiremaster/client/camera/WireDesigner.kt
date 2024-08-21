package com.buttersus.wiremaster.client.camera

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.extensions.toDegrees
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
import kotlin.math.*

@Environment(EnvType.CLIENT)
object WireDesigner {
    private val mc = MinecraftClient.getInstance()
    private val pos = Vector3d()
    private val velocity = Vector3d()
    private val lastInputVector = Vector3d()
    private val rotation = Quaterniond(0.0, 0.0, 0.0, 1.0)
    private val forwards = Vector3d(0.0, 0.0, 1.0)
    private val up = Vector3d(0.0, 1.0, 0.0)
    private val left = Vector3d(1.0, 0.0, 0.0)
    private var active = false
    private var wasInCursorMode = false
    private var cursorMode = false
    private var sprinting = false
    private var oldPerspective: Perspective? = null
    private var oldInput: Input? = null
    private var cameraInput: Input? = null
    private var yaw = 0.0
    private var pitch = 0.0
    private var lastTime = 0L
    private var gameRendererPicking = false

    fun init() {
        ClientTickEvents.START_CLIENT_TICK.register {
            onClientTickStart()
        }
    }

    // Wire Designer
    fun toggleWireDesigner(): Boolean {
        return when (active) {
            false -> enableWireDesigner()
            true -> disableWireDesigner()
        }
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
        val newPos = entity.getCameraPosVec(frameTime)
        pos.set(newPos.toVector3f())
        yaw = entity.getYaw(frameTime).toDouble()
        pitch = entity.getPitch(frameTime).toDouble()

        calculateVectors()

        val distance = -2.0
        val normalForwards = Vector3d(0.0, 0.0, 1.0).rotate(rotation)
        pos.add(Vector3d(normalForwards).mul(distance))
        velocity.set(0.0, 0.0, 0.0)
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
            if (WireMaster.MOVEMENT_TYPE == CameraMovementType.FLAT)
                Vector3d(-sin(yaw.toRadians()), 0.0, cos(yaw.toRadians()))
            else
                Vector3d(0.0, 0.0, 1.0).rotate(rotation)
        )
        up.set(
            if (WireMaster.MOVEMENT_TYPE == CameraMovementType.ABSOLUTE)
                Vector3d(0.0, 1.0, 0.0).rotate(rotation)
            else
                Vector3d(0.0, 1.0, 0.0)
        )
        left.set(1.0, 0.0, 0.0).rotate(rotation)
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

    private fun onClientTickStart() {
        if (!active) return
        val togglePerspectiveKey = mc.options.togglePerspectiveKey
        while (togglePerspectiveKey.wasPressed()) continue
        togglePerspectiveKey.isPressed = false
        oldInput?.tick(false, 0f)
    }

    fun onRenderTickStart() {
        if (!active || lastTime == 0L) {
            lastTime = System.nanoTime()
            return
        }
        val currentTime = System.nanoTime()
        val frameTime = (currentTime - lastTime) / 1e9
        lastTime = currentTime

        val input = oldInput ?: return
        val inputVector = Vector3d(
            input.pressingLeft.toDouble() - input.pressingRight.toDouble(),
            input.jumping.toDouble() - input.sneaking.toDouble(),
            input.pressingForward.toDouble() - input.pressingBack.toDouble()
        )

        // Transform input vector to world space
        val worldInputVector = Vector3d()
            .add(Vector3d(forwards).mul(inputVector.z))
            .add(Vector3d(left).mul(inputVector.x))
            .add(Vector3d(up).mul(inputVector.y))

        val slowdown = WireMaster.SLOWDOWN.pow(frameTime)

        if (worldInputVector.lengthSquared() > 0) {
            inputVector.normalize()
            worldInputVector.normalize()

            // Counter-strafing
            if (WireMaster.COUNTER_STRAFING && lastInputVector.dot(inputVector) == -1.0) {
                velocity.set(0.0, 0.0, 0.0)
            }

            val currentSpeed = velocity.length()
            if (currentSpeed > 0) {
                // Calculate the angle between current velocity and new input direction
                val angleBetween = acos(Vector3d(velocity).normalize().dot(worldInputVector)).toDegrees()

                // Adjust velocity based on the angle
                val turnFactor = when {
                    angleBetween > 120 -> 0.8  // Sharp turn
                    angleBetween > 60 -> 0.9   // Moderate turn
                    else -> 0.95               // Minor adjustment
                }

                velocity.mul(turnFactor)
                velocity.add(Vector3d(worldInputVector).mul((1 - turnFactor) * currentSpeed))
            }

            // Apply acceleration
            val acceleration = if (sprinting) WireMaster.ACCELERATION * 3.0 else WireMaster.ACCELERATION
            velocity.add(Vector3d(worldInputVector).mul(acceleration * frameTime))
            lastInputVector.set(inputVector)
        } else {
            // Apply slowdown when no input
            velocity.mul(slowdown)
        }

        // Clamp to max speed
        val maxSpeed = if (sprinting) WireMaster.MAX_SPEED * 2.0 else WireMaster.MAX_SPEED
        if (velocity.length() > maxSpeed) {
            velocity.normalize().mul(maxSpeed)
        }

        // Update position
        pos.add(Vector3d(velocity).mul(frameTime))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMouseScroll(mouseX: Double, mouseY: Double, scrollY: Double): Boolean {
        val scrollAmount = scrollY * mc.options.mouseSensitivity.value
        println("Scroll amount: $scrollAmount")
        return true
    }

    // Sprint
    fun onSprintPress() {
        sprinting = true
        println("Pressed")
    }

    fun onSprintRelease() {
        sprinting = false
        println("Released")
    }

    // Getters
    fun isActive() = active
    fun getXRot() = pitch
    fun getYRot() = yaw
    fun getX() = pos.x
    fun getY() = pos.y
    fun getZ() = pos.z

    // Other methods
    fun canToggleWireDesigner() =
        mc.currentScreen == null && mc.world != null && mc.player != null  // Not in a GUI

    fun canToggleCursorMode() =
        active && mc.currentScreen == null && mc.world != null && mc.player != null  // Active & not in a GUI

    fun canScroll() =
        active && mc.currentScreen == null && mc.world != null && mc.player != null  // Active & not in a GUI

    fun canToggleSprint() =
        mc.currentScreen == null && mc.world != null && mc.player != null  // Not in a GUI
}
