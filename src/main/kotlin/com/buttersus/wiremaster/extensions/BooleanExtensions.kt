package com.buttersus.wiremaster.extensions

@Suppress("unused")
fun Boolean.toDouble() =
    if (this) 1.0 else 0.0
