package com.buttersus.wiremaster.client.keybinding

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.designer.DesignerPermissions
import com.buttersus.wiremaster.client.event.KeyBindingEvents
import com.buttersus.wiremaster.util.passers.AtomicKeyStatePasser
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import de.siphalor.amecs.api.PriorityKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil
import java.util.concurrent.atomic.AtomicBoolean


@Environment(EnvType.CLIENT)
class CursorMovementKeyBinding : AmecsKeyBinding(
    "key.${WireMaster.MOD_ID}.cursor_movement",
    InputUtil.Type.KEYSYM,
    InputUtil.UNKNOWN_KEY.code,  // By default: Right Mouse
    "category.${WireMaster.MOD_ID}.keybindings",
    KeyModifiers()
), PriorityKeyBinding, AtomicKeyStatePasser {
    // Impl: AtomicKeyStatePasser
    private lateinit var atomicCursorMovementKeyHeld: AtomicBoolean

    override fun passAtomicKeyState(atomicKeyState: AtomicBoolean) {
        if (this::atomicCursorMovementKeyHeld.isInitialized) return
        this.atomicCursorMovementKeyHeld = atomicKeyState
    }

    override fun onPressedPriority(): Boolean {
        if (!DesignerPermissions.canHoldMovementControl()) return false
        atomicCursorMovementKeyHeld.set(true)
        KeyBindingEvents.CURSOR_MOVEMENT_PRESS.invoker().onCursorMovementKeyPress()
        return true
    }

    override fun onReleasedPriority(): Boolean {
        if (!DesignerPermissions.canHoldMovementControl()) return false
        atomicCursorMovementKeyHeld.set(false)
        KeyBindingEvents.CURSOR_MOVEMENT_RELEASE.invoker().onCursorMovementKeyRelease()
        return true
    }
}