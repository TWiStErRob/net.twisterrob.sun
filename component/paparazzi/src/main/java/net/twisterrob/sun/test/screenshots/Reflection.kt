package net.twisterrob.sun.test.screenshots

import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal val STATIC: Any? = null

internal operator fun Any?.get(field: Field): Any? =
	@Suppress("ExplicitCollectionElementAccessMethod") // Helper exists to solve exactly this.
	field.get(this)

internal operator fun Any?.set(field: Field, value: Any?) {
	@Suppress("ExplicitCollectionElementAccessMethod") // Helper exists to solve exactly this.
	field.set(this, value)
}

@Throws(NoSuchFieldException::class, IllegalAccessException::class)
internal fun Field.clearFinal() {
	// Java 8-17 compatible version of: Field::class.java.getDeclaredField("modifiers")
	// Idea: https://stackoverflow.com/a/69418150/253468
	@Suppress("UnnecessaryLet") // TODEL https://github.com/detekt/detekt/issues/5701
	Class::class.java
		.getDeclaredMethod("getDeclaredFields0", Boolean::class.javaPrimitiveType)
		.apply { isAccessible = true }
		.invoke(Field::class.java, false)
		.let { @Suppress("UNCHECKED_CAST") (it as Array<Field>) }
		.single { it.name == "modifiers" }
		// Then clear the final modifier.
		.apply { isAccessible = true }
		.setInt(this, this.modifiers and Modifier.FINAL.inv())
}
