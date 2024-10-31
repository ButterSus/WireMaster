package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.event.GameRendererTickEvents
import com.buttersus.wiremaster.util.atomic.ObservableAtomicReferenceWrapper
import com.buttersus.wiremaster.util.delegates.getValue
import com.buttersus.wiremaster.util.delegates.setValue
import com.buttersus.wiremaster.util.interfaces.Initializable
import com.buttersus.wiremaster.util.interfaces.Toggleable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import org.joml.Vector3d
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

@Suppress("MemberVisibilityCanBePrivate", "EmptyMethod")
@Environment(EnvType.CLIENT)
class CameraController(
    private val mc: MinecraftClient,

    atomicCameraActive: AtomicBoolean,
    atomicGameRendererPicking: AtomicBoolean,

    // Keys
    atomicSprintKeyHeld: AtomicBoolean,

    // Fields
    val position: Vector3d,
    val velocity: Vector3d,

    // Params
    atomicYaw: ObservableAtomicReferenceWrapper<Float>,
    atomicPitch: ObservableAtomicReferenceWrapper<Float>,

    ) : GameRendererTickEvents.StartRenderWithFrameTimeTick, Initializable, Toggleable {
    // Fields
    private val sprintKeyHeld by atomicSprintKeyHeld
    var yaw: Float by atomicYaw
        private set
    var pitch: Float by atomicPitch
        private set

    // Impl: Initializable
    override fun init() {
        GameRendererTickEvents.START_RENDER_WITH_FRAME_TIME_TICK.register(this)
    }

    // Methods
    private fun setInitialPosition() {
        val entity = mc.getCameraEntity() ?: throw IllegalStateException("Camera is null")

        // Get initial values
        val initialPosition = Designer.cameraCalculator.calculateInitialPosition(entity)
        val (initialYaw, initialPitch) = Designer.cameraCalculator.calculateInitialYawPitch(entity)

        // Set initial values
        position.set(initialPosition)
        yaw = initialYaw
        pitch = initialPitch

        // Reset frame time so its accumulated correctly
        GameRendererTickEvents.resetFrameTime()

        // Invalidate caches
        Designer.invalidateAllCaches()

        // Reset velocity
        velocity.set(0.0)
    }

    private fun counterStrafe() {
        if (!WireMaster.COUNTER_STRAFING) return
        if (Designer.inputCalculator.isCounterStrafing()) velocity.set(0.0)
    }

    private fun adjustVelocityDirection(worldInputUnitVector: Vector3d, frameTime: Double) {
        val turnFactor = Designer.cameraCalculator.calculateTurnFactor(worldInputUnitVector).pow(frameTime)
        velocity.mul(turnFactor)
        val velocityLength = velocity.length()
        velocity.add(Vector3d(worldInputUnitVector).mul(1 - turnFactor).mul(velocityLength))
    }

    private fun clampVelocity() {
        val maxVelocity = WireMaster.MAX_SPEED
        if (velocity.length() > maxVelocity) velocity.normalize().mul(maxVelocity)
    }

    private fun accelerate(worldInputUnitVector: Vector3d, frameTime: Double) {
        val factor = if (sprintKeyHeld) 3.0 else 1.0
        val acceleration = WireMaster.ACCELERATION * factor
        velocity.add(Vector3d(worldInputUnitVector).mul(acceleration * frameTime))
    }

    private fun slowdown(frameTime: Double) {
        val slowdown = WireMaster.SLOWDOWN.pow(frameTime)
        velocity.mul(slowdown)
    }

    private fun updatePosition(frameTime: Double) {
        position.add(Vector3d(velocity).mul(frameTime))
    }

    // Impl: Toggleable
    override fun enable() {
        // Do nothing if already active
        if (isActive()) return

        // Override input & perspective
        Designer.inputController.overrideInput()
        Designer.rendererController.overridePerspective()

        setInitialPosition()
        Designer.invalidateAllCaches()

        active = true
    }

    override fun disable() {
        // Do nothing if already inactive
        if (!isActive()) return

        // Disable cursor if active
        if (Designer.cursorController.isActive())
            Designer.cursorController.disable()

        // Restore input & perspective
        Designer.inputController.restoreInput()
        Designer.rendererController.restorePerspective()

        active = false
    }

    @get:Deprecated("Use isActive() instead")
    override var active by atomicCameraActive
        private set

    // Impl: Events
    override fun onStartRenderWithFrameTimeTick(tickDelta: Float, startTime: Long, tick: Boolean, frameTime: Double) {
        if (!isActive()) return
        val worldInputUnitVector = Designer.cameraCalculator.calculateWorldInputUnitVector()
        if (Designer.inputCalculator.isInputActive()) {
            if (Designer.cameraCalculator.isMoving()) {
                counterStrafe()
                adjustVelocityDirection(worldInputUnitVector, frameTime)
            }
            accelerate(worldInputUnitVector, frameTime)
        } else slowdown(frameTime)
        println("Velocity: $velocity;\nPosition: $position;")
        clampVelocity()
        updatePosition(frameTime)
    }

    // Overrides: Camera entity position
    @set:JvmName("setGameRendererPickingInternal")
    private var gameRendererPicking by atomicGameRendererPicking

    fun setGameRendererPicking(gameRendererPicking: Boolean) {
        this.gameRendererPicking = gameRendererPicking
    }

    fun shouldOverrideCameraEntityPosition(entity: Entity): Boolean {
        return gameRendererPicking && isActive() && (entity == mc.getCameraEntity())
    }
}
