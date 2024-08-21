package com.buttersus.wiremaster.extensions

import kotlin.math.PI

fun Double.toRadians() = this * PI / 180.0
fun Double.toDegrees() = this * 180.0 / PI
