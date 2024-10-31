package com.buttersus.wiremaster.util.passers

import java.util.concurrent.atomic.AtomicBoolean

fun interface AtomicKeyStatePasser {
    fun passAtomicKeyState(atomicKeyState: AtomicBoolean)
}

