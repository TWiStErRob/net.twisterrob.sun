package net.twisterrob.sun.test.screenshots

import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal val STATIC: Any? = null

internal operator fun Any?.get(field: Field): Any? =
	field.get(this)

internal operator fun Any?.set(field: Field, value: Any?) {
	field.set(this, value)
}

@Throws(NoSuchFieldException::class, IllegalAccessException::class)
internal fun Field.clearFinal() {
	Field::class.java
		.getDeclaredField("modifiers")
		.apply { isAccessible = true }
		.setInt(this, this.modifiers and Modifier.FINAL.inv())
}
