package com.buttersus.wiremaster.client.camera

import com.buttersus.wiremaster.extensions.toDouble
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.pow

@Environment(EnvType.CLIENT)
object WireDesigner {
    private val mc = MinecraftClient.getInstance()
    private var rotation = Quaternionf(0.0f, 0.0f, 0.0f, 1.0f)
    private var forwards = Vector3f(0.0f, 0.0f, 1.0f)
    private var up = Vector3f(0.0f, 1.0f, 0.0f)
    private var left = Vector3f(1.0f, 0.0f, 0.0f)
    private var active = false
    private var oldPerspective: Perspective? = null
    private var oldInput: Input? = null
    private var cameraInput: Input? = null
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0
    private var yaw = 0.0f
    private var pitch = 0.0f
    private var forwardVelocity = 0.0
    private var leftVelocity = 0.0
    private var upVelocity = 0.0
    private var lastTime = 0L
    private var gameRendererPicking = false

    // Enable / disable methods
    fun toggle() = when (active) {
        false -> enable()
        true -> disable()
    }

    fun disable() {
        if (!active) return
        val player = mc.player ?: return
        active = false

        val cameraType = mc.options.perspective
        mc.options.perspective = oldPerspective
        player.input = oldInput
        if (cameraType.isFirstPerson != mc.options.perspective.isFirstPerson)
            mc.gameRenderer.onCameraEntitySet(if (mc.options.perspective.isFirstPerson) mc.getCameraEntity() else null)
        oldPerspective = null
    }

    fun enable() {
        if (active) return
        val player = mc.player ?: return
        val entity = mc.getCameraEntity() ?: return
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
        yaw = entity.getYaw(frameTime)
        pitch = entity.getPitch(frameTime)

        calculateVectors()

        val distance = -2.0
        x += forwards.x() * distance
        y += forwards.y() * distance
        z += forwards.z() * distance

        forwardVelocity = 0.0
        leftVelocity = 0.0
        upVelocity = 0.0
        lastTime = 0L
    }

    // Math
    private fun calculateVectors() {
        rotation.rotationYXZ(-yaw * (PI.toFloat() / 180f), pitch * (PI.toFloat() / 180F), 0.0F)
        forwards.set(0.0F, 0.0F, 1.0F).rotate(rotation)
        up.set(0.0F, 1.0F, 0.0F).rotate(rotation)
        left.set(1.0F, 0.0F, 0.0F).rotate(rotation)
    }

    @Suppress("LocalVariableName")
    private fun combineMovement(
        _velocity: Double, impulse: Double, frameTime: Double,
        acceleration: Double, slowdown: Double
    ): Double {
        var velocity = _velocity
        if (impulse != 0.0) {
            if (impulse > 0 && velocity < 0) {
                velocity = 0.0
            }
            if (impulse < 0 && velocity > 0) {
                velocity = 0.0
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
            pitch += xRot.toFloat() * 0.15f
            yaw += yRot.toFloat() * 0.15f
            pitch = MathHelper.clamp(pitch, -90f, 90f)
            calculateVectors()
        } else player.changeLookDirection(yRot, xRot)
    }

    fun onWorldUnload() = disable()
    fun onRenderCrosshairIsFirstPerson(cameraType: Perspective) = active || cameraType.isFirstPerson
    fun onRenderItemInHandIsFirstPerson(cameraType: Perspective) = active || cameraType.isFirstPerson
    fun onBeforeGameRendererPick() { gameRendererPicking = true }
    fun onAfterGameRendererPick() { gameRendererPicking = false }
    fun shouldOverrideCameraEntityPosition(entity: Entity) = active && (entity == mc.getCameraEntity() && gameRendererPicking)

    fun onClientTickStart() {
        if (!active) return
        val togglePerspectiveKey = mc.options.togglePerspectiveKey
        while (togglePerspectiveKey.wasPressed()) { continue }
        togglePerspectiveKey.isPressed = false
        oldInput?.tick(false, 0f)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRenderTickStart(partialTicks: Float) {
        if (!active) return
        if (lastTime == 0L) return Unit.also { lastTime = System.nanoTime() }
        val currentTime = System.nanoTime()
        val frameTime = (currentTime - lastTime) / 1e9
        lastTime = currentTime

        val input = oldInput ?: return
        val forwardImpulse = input.pressingForward.toDouble() - input.pressingBack.toDouble()
        val leftImpulse = input.pressingLeft.toDouble() - input.pressingRight.toDouble()
        val upImpulse = input.jumping.toDouble() - input.sneaking.toDouble()
        val slowdown = 0.01.pow(frameTime)
        forwardVelocity = combineMovement(forwardVelocity, forwardImpulse, frameTime, 50.0, slowdown)
        leftVelocity = combineMovement(leftVelocity, leftImpulse, frameTime, 50.0, slowdown)
        upVelocity = combineMovement(upVelocity, upImpulse, frameTime, 50.0, slowdown)

        var dx = (forwards.x().toDouble() * forwardVelocity + left.x().toDouble() * leftVelocity) * frameTime
        var dy = (forwards.y().toDouble() * forwardVelocity + left.y().toDouble() * leftVelocity) * frameTime
        var dz = (forwards.z().toDouble() * forwardVelocity + left.z().toDouble() * leftVelocity) * frameTime

        val speed = Vec3d(dx, dy, dz).length() / frameTime
        if (speed > 50) {
            val factor = 50 / speed
            forwardVelocity *= factor
            leftVelocity *= factor
            upVelocity *= factor
            dx *= factor
            dy *= factor
            dz *= factor
        }

        x += dx
        y += dy
        z += dz
    }

    // Getters
    fun isActive() = active
    fun getXRot() = pitch
    fun getYRot() = yaw
    fun getX() = x
    fun getY() = y
    fun getZ() = z
}