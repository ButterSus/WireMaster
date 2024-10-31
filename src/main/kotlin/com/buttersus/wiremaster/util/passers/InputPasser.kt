package com.buttersus.wiremaster.util.passers

import net.minecraft.client.input.Input

fun interface InputPasser {
    fun passInput(input: Input)
}