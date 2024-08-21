package com.buttersus.wiremaster.client.camera

enum class CameraMovementType {
    NORMAL,  // Jumping & Sneaking changes only Y-axis; Pressing WASD changes all three axes
    FLAT,  // Jumping & Sneaking changes only Y-axis; Pressing WASD changes only XZ axes
    ABSOLUTE,  // Jumping, Sneaking and pressing WASD changes all three axes
}