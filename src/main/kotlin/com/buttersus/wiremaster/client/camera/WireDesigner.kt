package com.buttersus.wiremaster.client.camera

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.camera.interpolation.ExponentialInterpolation
import com.buttersus.wiremaster.client.camera.interpolation.SpringInterpolation
import com.buttersus.wiremaster.extensions.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.entity.Entity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import org.joml.Matrix4f
import org.joml.Quaterniond
import org.joml.Vector2d
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
    private val absoluteForwards = Vector3d(0.0, 0.0, 1.0)
    private val up = Vector3d(0.0, 1.0, 0.0)
    private val absoluteUp = Vector3d(0.0, 1.0, 0.0)
    private val left = Vector3d(1.0, 0.0, 0.0)
    private val absoluteLeft = Vector3d(1.0, 0.0, 0.0)
    private val mouseDeltas = Vector2d(0.0, 0.0)
    private val scrollInterpolation = SpringInterpolation.createBasic(pos)
    private val cursorInterpolation = ExponentialInterpolation.createQuick(pos)
    private var cursorTargetedPos: Vector3d? = null
    private var cursorTargetedHitResult: HitResult? = null
    private var active = false
    private var wasInCursorMode = false
    private var cursorMode = false
    private var sprintHold = false
    private var movementControlHold = false
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
        val player = mc.player ?: throw IllegalStateException("Player is null")
        val entity = mc.getCameraEntity() ?: throw IllegalStateException("Camera is null")
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
        val flatForwards = Vector3d(-sin(yaw.toRadians()), -0.5, cos(yaw.toRadians()))
        pos.add(Vector3d(flatForwards).mul(distance))
        velocity.set(0.0, 0.0, 0.0)
        lastTime = 0L

        val direction = Vector3d(player.eyePos.toVector3f()).sub(pos).normalize()
        val (newYaw, newPitch) = direction.lookAt()
        yaw = newYaw.toDegrees()
        pitch = newPitch.toDegrees()

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
        movementControlHold = false
        cursorTargetedPos = null
        cursorTargetedHitResult = null
        cursorInterpolation.reset()
        scrollInterpolation.reset()
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

        // Customizable logic
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

        // Do not modify behaviour
        absoluteForwards.set(Vector3d(0.0, 0.0, 1.0).rotate(rotation))
        absoluteUp.set(0.0, 1.0, 0.0).rotate(rotation)
        absoluteLeft.set(1.0, 0.0, 0.0).rotate(rotation)
    }

    // Events && Mixin Methods
    fun getCursorVector(mouseVector: Vector3d): Vec3d {
        val mc = MinecraftClient.getInstance()
        val player = mc.player ?: throw IllegalStateException("Player is null")
        val vector = mouseVector.toVector3f()

        Matrix4f()
            .set(mc.gameRenderer.getBasicProjectionMatrix((player.fovMultiplier * mc.options.fov.value).toDouble()))
            .invert()  // Compute view vector from NDC space
            .transformPosition(vector)
        vector.normalize()  // Normalize vector
        Matrix4f()
            .rotate(mc.gameRenderer.camera.rotation)  // Rotate vector
            .transformPosition(vector)
        vector.negate()  // Flip vector

        // Return direction vector
        return vector.toVec3d()
    }

    fun getMouseVector(): Vector3d {
        val mc = MinecraftClient.getInstance()
        val window = mc.window ?: throw IllegalStateException("Window is null")

        // Return NDC coordinates vector
        return Vector3d(
            (mc.mouse.x / window.width * 2.0 - 1.0),
            (mc.mouse.y / window.height * 2.0 - 1.0),
            1.0
        )
    }

    fun onPlayerTurn(player: ClientPlayerEntity, yRot: Double, xRot: Double) {
        if (active) {
            if (!cursorMode) {
                pitch += xRot.toFloat() * 0.15f
                yaw += yRot.toFloat() * 0.15f
                pitch = MathHelper.clamp(pitch, -90.0, 90.0)  // Player can't turn 180 degrees vertically
            } else {
                mouseDeltas.add(
                    yRot / mc.window.width * 2,
                    xRot / mc.window.height * 2
                )  // Rotation about Y axis is horizontal movement and vice versa
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

        // Compute movement and apply it
        if (movementControlHold) handleMouseInputMovement()
        handleKeyboardInputMovement(frameTime)

        // Interpolations
        scrollInterpolation.update(frameTime)
        cursorInterpolation.update(frameTime)
    }

    private fun handleKeyboardInputMovement(frameTime: Double) {
        val input = oldInput ?: return
        val inputVector = Vector3d(
            input.pressingLeft.toDouble() - input.pressingRight.toDouble(),
            input.jumping.toDouble() - input.sneaking.toDouble(),
            input.pressingForward.toDouble() - input.pressingBack.toDouble()
        )

        // Transform input vector to world space
        val worldInputVector = if (cursorMode)
            Vector3d()
                .add(Vector3d(absoluteUp).mul(inputVector.z))
                .add(Vector3d(absoluteLeft).mul(inputVector.x))
        else
            Vector3d()
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
            val acceleration = if (sprintHold) WireMaster.ACCELERATION * 3.0 else WireMaster.ACCELERATION
            velocity.add(Vector3d(worldInputVector).mul(acceleration * frameTime))
            lastInputVector.set(inputVector)
        } else {
            // Apply slowdown when no input
            velocity.mul(slowdown)
        }

        // Clamp to max speed
        val maxSpeed = if (sprintHold) WireMaster.MAX_SPEED * 2.0 else WireMaster.MAX_SPEED
        if (velocity.length() > maxSpeed) {
            velocity.normalize().mul(maxSpeed)
        }

        // Update position
        pos.add(Vector3d(velocity).mul(frameTime))
    }

    private fun handleMouseInputMovement() {
        // Calculate delta of positions
        val targetedPos = cursorTargetedPos ?: throw IllegalStateException("Targeted position is null")
        val targetedPosDelta = Vector3d(targetedPos).sub(pos).sub(cursorInterpolation.get())

        // Get the 2nd cursor vector and perform raycast
        val newCursorVector =
            getCursorVector(getMouseVector().add(Vector3d(mouseDeltas.x, mouseDeltas.y, 0.0))).toVector3d()
        val newTargetedPointDelta =
            Vector3d(newCursorVector).mul(absoluteForwards.dot(targetedPosDelta) / absoluteForwards.dot(newCursorVector))

        // Apply delta to interpolation
        val deltaPos = Vector3d(targetedPosDelta).sub(newTargetedPointDelta)
        cursorInterpolation.add(deltaPos)

        // Reset mouse deltas
        mouseDeltas.set(0.0, 0.0)
    }

    fun onMouseScroll(scrollY: Double): Boolean {
        val scrollVector = Vector3d()
            .add(Vector3d(absoluteForwards).mul(scrollY))
        scrollInterpolation.add(scrollVector)
        return true
    }

    // Sprint
    fun onSprintPress() {
        sprintHold = true
    }

    fun onSprintRelease() {
        sprintHold = false
    }

    // Movement Control
    fun onMovementControlPress(): Boolean {
        movementControlHold = true
        mouseDeltas.set(0.0, 0.0)

        // == Compute and save targeted position into variable ==

        val world = mc.world ?: throw IllegalStateException("World is null")
        val interactionManager = mc.interactionManager ?: throw IllegalStateException("Interaction Manager is null")

        // Get the cursor direction vector
        val cursorVector = getCursorVector(getMouseVector()).toVector3d()
        val reachDistance = interactionManager.reachDistance.toDouble()
        val deltaVector = Vector3d()
            .add(Vector3d(cursorVector).mul(reachDistance))

        // Perform raycast to determine the targeted point in 3D space
        val blockHitResult = world.raycast(
            RaycastContext(
                pos.toVec3d(),
                Vector3d(pos).add(deltaVector).toVec3d(),
                RaycastContext.ShapeType.COLLIDER,  // Only block collisions
                RaycastContext.FluidHandling.ANY,  // Include fluids
                mc.player
            )
        )

        // Check if it's a block
        when (blockHitResult.type) {
            HitResult.Type.MISS -> return true
            HitResult.Type.BLOCK -> {
                cursorTargetedPos = blockHitResult.pos.toVector3d()
                cursorTargetedHitResult = blockHitResult
            }

            else -> throw IllegalStateException("Unexpected hit result type")
        }

        return true
    }

    fun onMovementControlRelease(): Boolean {
        movementControlHold = false
        cursorTargetedPos = null
        cursorTargetedHitResult = null
        return true
    }

    // Getters
    fun isActive() = active
    fun isCursorMode() = cursorMode
    fun getXRot() = pitch
    fun getYRot() = yaw
    fun getX() = pos.x
    fun getY() = pos.y
    fun getZ() = pos.z
    fun getVec3dPos() = pos.toVec3d()
    fun getMouseTargetedHitResult() = cursorTargetedHitResult

    // Other methods
    fun canToggleWireDesigner() =
        mc.currentScreen == null && mc.world != null && mc.player != null  // Not in a GUI

    fun canToggleCursorMode() =
        active && mc.currentScreen == null && mc.world != null && mc.player != null  // Active & not in a GUI

    fun canScroll() =
        cursorMode && mc.currentScreen == null && mc.world != null && mc.player != null  // Active & not in a GUI

    fun canHoldSprint() =
        mc.currentScreen == null && mc.world != null && mc.player != null  // Not in a GUI

    fun canHoldMovementControl() =
        cursorMode && mc.currentScreen == null && mc.world != null && mc.player != null  // Not in a GUI
}
