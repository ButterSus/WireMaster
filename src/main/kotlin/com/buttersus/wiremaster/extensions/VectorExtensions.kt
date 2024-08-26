@file:Suppress("unused", "RedundantSuppression")
package com.buttersus.wiremaster.extensions

import net.minecraft.util.math.Vec3d
import org.joml.Vector3d
import org.joml.Vector3f
import kotlin.math.*

fun Vector3d.lookAt(): Pair<Double, Double> {
    val distance = sqrt(this.x * this.x + this.z * this.z)
    val yaw = -atan2(this.x, this.z)
    val pitch = atan2(-this.y, distance)
    return yaw to pitch
}


fun Vector3d.toVec3d(): Vec3d {
    return Vec3d(this.x, this.y, this.z)
}

fun Vector3f.toVec3d(): Vec3d {
    return Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}
