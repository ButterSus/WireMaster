package com.buttersus.wiremaster.util.delegates

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KProperty

operator fun AtomicBoolean.getValue(thisRef: Any?, property: KProperty<*>): Boolean = get()
operator fun AtomicBoolean.setValue(thisRef: Any?, property: KProperty<*>, value: Boolean): Unit = set(value)
