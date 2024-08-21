package com.buttersus.wiremaster.extensions

import org.joml.Vector3d
import kotlin.math.*

fun Vector3d.lookAt(): Pair<Double, Double> {
    val distance = sqrt(this.x * this.x + this.z * this.z)
    val yaw = -atan2(this.x, this.z)
    val pitch = atan2(-this.y, distance)
    return yaw to pitch
}
