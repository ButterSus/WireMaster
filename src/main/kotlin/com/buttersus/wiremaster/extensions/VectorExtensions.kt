package com.buttersus.wiremaster.extensions

import org.joml.Vector3d

fun Vector3d.clamp(min: Double, max: Double): Vector3d = when {
    this.length() < min -> this.normalize().mul(min)
    this.length() > max -> this.normalize().mul(max)
    else -> this
}