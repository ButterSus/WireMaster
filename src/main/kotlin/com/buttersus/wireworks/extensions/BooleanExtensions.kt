package com.buttersus.wireworks.extensions

@Suppress("unused")
fun Boolean.toInt() =
    if (this) 1 else 0
