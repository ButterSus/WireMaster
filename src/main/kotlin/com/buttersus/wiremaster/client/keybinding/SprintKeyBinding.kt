package com.buttersus.wiremaster.client.keybinding

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.designer.DesignerPermissions
import com.buttersus.wiremaster.util.passers.AtomicKeyStatePasser
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil
import java.util.concurrent.atomic.AtomicBoolean

@Environment(EnvType.CLIENT)
class SprintKeyBinding : AmecsKeyBinding(
    "key.${WireMaster.MOD_ID}.sprint",
    InputUtil.Type.KEYSYM,
    InputUtil.UNKNOWN_KEY.code,  // By default: Left Shift
    "category.${WireMaster.MOD_ID}.keybindings",
    KeyModifiers()
), AtomicKeyStatePasser {
    // Impl: AtomicKeyStatePasser
    private lateinit var atomicSprintKeyHeld: AtomicBoolean

    override fun passAtomicKeyState(atomicKeyState: AtomicBoolean) {
        if (this::atomicSprintKeyHeld.isInitialized) return
        this.atomicSprintKeyHeld = atomicKeyState
    }

    override fun onPressed() {
        if (!DesignerPermissions.canHoldSprint()) return
        atomicSprintKeyHeld.set(true)
    }

    override fun onReleased() {
        if (!DesignerPermissions.canHoldSprint()) return
        atomicSprintKeyHeld.set(false)
    }
}