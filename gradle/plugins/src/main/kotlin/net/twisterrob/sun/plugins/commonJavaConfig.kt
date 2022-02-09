package net.twisterrob.sun.plugins

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType

internal fun Project.commonJavaConfig() {
	tasks.withType<JavaCompile> {
		options.compilerArgs = options.compilerArgs + listOf(
			// Enable all warnings during compilation.
			"-Xlint:all",
			// Workaround for https://github.com/cashapp/paparazzi/issues/362.
			"-Xlint:-classfile",
			// Fail build when warnings pop up.
			"-Werror",
		)
	}
}
