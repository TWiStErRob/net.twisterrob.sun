package net.twisterrob.sun.test.screenshots

import java.lang.reflect.Field
import java.lang.reflect.Modifier

@Throws(NoSuchFieldException::class, IllegalAccessException::class)
internal fun Field.clearFinal() {
	Field::class.java
		.getDeclaredField("modifiers")
		.apply { isAccessible = true }
		.setInt(this, this.modifiers and Modifier.FINAL.inv())
}
