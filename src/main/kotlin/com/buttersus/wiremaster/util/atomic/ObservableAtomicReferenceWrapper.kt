package com.buttersus.wiremaster.util.atomic

import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class ObservableAtomicReferenceWrapper<T>(
    initialValue: T,
    private val onChangeListener: ((T) -> Unit)
) {
    // Fields
    private val atomicReference = AtomicReference(initialValue)

    // Methods
    fun set(newValue: T) {
        val oldValue = atomicReference.getAndSet(newValue)
        if (oldValue != newValue) {
            onChangeListener.invoke(newValue)
        }
    }

    fun get(): T = atomicReference.get()

    fun unwrap(): AtomicReference<T> = atomicReference

    // Secondary constructors
    constructor(initialValue: T, onChangeListener: () -> Unit) : this(initialValue, { _ -> onChangeListener() })

    // Delegates
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
}
