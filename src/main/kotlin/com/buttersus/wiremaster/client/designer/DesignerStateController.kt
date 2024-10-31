package com.buttersus.wiremaster.client.designer

import java.util.concurrent.atomic.AtomicBoolean

class DesignerStateController(
    private val atomicCameraActive: AtomicBoolean,
    private val atomicCursorActive: AtomicBoolean
) {
    val state: DesignerState
        get() = when {
            atomicCursorActive.get() -> DesignerState.NAVIGATE
            atomicCameraActive.get() -> DesignerState.FLY
            else -> DesignerState.NORMAL
        }
}