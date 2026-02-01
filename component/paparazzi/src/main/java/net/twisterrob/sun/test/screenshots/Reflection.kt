package net.twisterrob.sun.test.screenshots

import java.lang.reflect.Field

internal val STATIC: Any? = null

internal operator fun Any?.get(field: Field): Any? =
	field.get(this)

internal operator fun Any?.set(field: Field, value: Any?) {
	@Suppress("ExplicitCollectionElementAccessMethod") // Helper exists to solve exactly this.
	field.set(this, value)
}
