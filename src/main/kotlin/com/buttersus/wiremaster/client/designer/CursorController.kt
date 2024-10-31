package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.client.event.KeyBindingEvents
import com.buttersus.wiremaster.client.event.MinecraftClientEvents
import com.buttersus.wiremaster.client.event.MouseEvents
import com.buttersus.wiremaster.util.VectorMathUtils
import com.buttersus.wiremaster.util.atomic.ObservableAtomicReferenceWrapper
import com.buttersus.wiremaster.util.delegates.getValue
import com.buttersus.wiremaster.util.delegates.setValue
import com.buttersus.wiremaster.util.interfaces.Initializable
import com.buttersus.wiremaster.util.interfaces.Toggleable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.RaycastContext
import org.joml.Vector3d
import java.util.concurrent.atomic.AtomicBoolean

@Environment(EnvType.CLIENT)
class CursorController(
    private val mc: MinecraftClient,

    // Keys
    private val position: Vector3d,

    // Params
    atomicCursorActive: AtomicBoolean,
    atomicCursorMovementKeyHeld: AtomicBoolean,
    atomicYaw: ObservableAtomicReferenceWrapper<Float>,
    atomicPitch: ObservableAtomicReferenceWrapper<Float>,
) : Toggleable, Initializable, MinecraftClientEvents.ScreenOpen, MinecraftClientEvents.ScreenClose,
    MouseEvents.PlayerTurn, MouseEvents.MouseMove, MouseEvents.MouseScrollY,
    KeyBindingEvents.CursorMovementKeyPress, KeyBindingEvents.CursorMovementKeyRelease {
    // Fields
    private var cursorMovementKeyHeld by atomicCursorMovementKeyHeld
    private var yaw by atomicYaw
    private var pitch by atomicPitch

    // Impl: Initializable
    override fun init() {
        MinecraftClientEvents.SCREEN_OPEN.register(this)
        MinecraftClientEvents.SCREEN_CLOSE.register(this)
        MouseEvents.PLAYER_TURN.register(this)
        MouseEvents.MOUSE_MOVE.register(this)
        MouseEvents.MOUSE_SCROLL_Y.register(this)
        KeyBindingEvents.CURSOR_MOVEMENT_PRESS.register(this)
        KeyBindingEvents.CURSOR_MOVEMENT_RELEASE.register(this)
    }

    // Methods
    var hoverHitResult: HitResult? = null
        private set
    private val hoverPosition: Vector3d? get() = hoverHitResult?.pos?.let(VectorMathUtils::toVector3d)

    private fun resetHover() {
        hoverHitResult = null
    }

    private fun computeHitResult(viewDirection: Vector3d): HitResult? {
        val world = mc.world ?: throw IllegalStateException("World is null")
        return world.raycast(
            RaycastContext(
                VectorMathUtils.toVec3d(position),
                VectorMathUtils.toVec3d(Vector3d(position).add(viewDirection.mul(reachDistance))),
                RaycastContext.ShapeType.COLLIDER,  // Only block collisions
                RaycastContext.FluidHandling.ANY,  // Include fluids
                mc.player
            )
        )?.let {
            when (it.type) {
                HitResult.Type.MISS -> null
                HitResult.Type.BLOCK -> it
                else -> throw IllegalStateException("Unexpected hit result type: ${it.type}")
            }
        }
    }

    // Public fields
    @Suppress("MemberVisibilityCanBePrivate")
    val reachDistance: Double
        get() = mc.interactionManager?.reachDistance?.toDouble()
            ?: throw IllegalStateException("Interaction Manager is null")

    @Suppress("MemberVisibilityCanBePrivate")
    val instantaneousPosition: Vector3d
        get() = Vector3d(position).add(Designer.interpolationManager.cursorMovementInterpolation.getVector())

    // Impl: Toggleable
    override fun enable() {
        Designer.rendererController.showCursor()
        active = true
    }

    override fun disable() {
        Designer.rendererController.hideCursor()
        cursorMovementKeyHeld = false
        resetHover()
        Designer.interpolationManager.resetForCursor()
        active = false
        shouldDisableCursor = false
    }

    @get:Deprecated("Use isActive() instead")
    override var active by atomicCursorActive
        private set

    // Impl: Events
    var shouldDisableCursor = false  // Window will break cursor movement
        private set

    override fun onScreenOpen(screen: Screen, isInGame: Boolean, level: Int) {
        if (!isInGame || level != 0 || !isActive()) return
        Designer.rendererController.hideCursor()
        shouldDisableCursor = true
    }

    override fun onScreenClose(screen: Screen, isInGame: Boolean, level: Int) {
        if (!isInGame || level != 1 || !isActive()) return
        Designer.rendererController.showCursor()
        shouldDisableCursor = false
    }

    override fun onPlayerTurn(player: ClientPlayerEntity, yRot: Double, xRot: Double) {
        // Default behavior
        if (!Designer.cameraController.isActive()) {
            player.changeLookDirection(yRot, xRot)
            return
        }

        // Camera controller behavior
        if (!isActive()) {
            pitch += xRot.toFloat() * 0.15f
            yaw += yRot.toFloat() * 0.15f

            pitch = MathHelper.clamp(pitch, -90.0f, 90.0f)
        }

        // Invalidate all caches
        Designer.invalidateAllCaches()
    }

    override fun onCursorMovementKeyPress() {
        if (!isActive()) return
        val viewDirection = Designer.cursorCalculator.calculateViewDirectionFromMouse()
        hoverHitResult = computeHitResult(viewDirection)
    }

    override fun onCursorMovementKeyRelease() {
        if (!isActive())
        resetHover()
    }

    override fun onMouseMove(x: Double, y: Double) {
        if (!isActive() || !cursorMovementKeyHeld) return

        val localHoverOffset = hoverPosition?.let {
            Vector3d(hoverPosition).sub(instantaneousPosition)
        } ?: throw IllegalStateException("Hover position is null")

        val nextHoverOffset = Designer.cursorCalculator.projectVectorOntoPlane(
            Designer.cursorCalculator.calculateViewDirectionFromMouse(), localHoverOffset
        )

        Designer.interpolationManager.cursorMovementInterpolation.addVector(localHoverOffset.sub(nextHoverOffset))
    }

    override fun onScrollY(scrollY: Double) {
        if (!isActive()) return
        val scrollOffset = Designer.cursorCalculator.calculateScrollOffset(scrollY)
        Designer.interpolationManager.mouseScrollInterpolation.addVector(scrollOffset)
    }
}