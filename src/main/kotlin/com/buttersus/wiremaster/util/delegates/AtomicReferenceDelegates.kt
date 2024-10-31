package com.buttersus.wiremaster.util.delegates

import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

operator fun <V> AtomicReference<V>.getValue(thisRef: Any?, property: KProperty<*>): V = get()
operator fun <V> AtomicReference<V>.setValue(thisRef: Any?, property: KProperty<*>, value: V) = set(value)
