package com.buttersus.wiremaster.util.interfaces

interface Toggleable {
    // Main method
    fun toggle() {
        when (isActive()) {
            false -> enable()
            true -> disable()
        }
    }

    fun enable()
    fun disable()

    // Getters
    @get:Deprecated("Use isActive() instead")
    val active: Boolean

    @Suppress("DEPRECATION")
    fun isActive(): Boolean = active
}