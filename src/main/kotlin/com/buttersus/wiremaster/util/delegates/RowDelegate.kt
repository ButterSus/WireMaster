package com.buttersus.wiremaster.util.delegates

import org.joml.Matrix3d
import org.joml.Vector3d
import kotlin.reflect.KProperty

/**
 * Creates new instance of Vector3d by row
 */
class RowDelegate(
    private val matrix: Matrix3d,
    private val rowIndex: Int
) {
    @Suppress("unused")
    companion object {
        const val UP = 0
        const val RIGHT = 1
        const val FORWARD = 2

        const val X = 0
        const val Y = 1
        const val Z = 2
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vector3d) {
        matrix.setRow(rowIndex, value)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Vector3d {
        return matrix.getRow(rowIndex, Vector3d())
    }
}