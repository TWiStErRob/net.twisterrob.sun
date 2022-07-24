package net.twisterrob.sun.plugins.internal

import net.twisterrob.sun.plugins.isCI
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class StrictCompilationPlugin: Plugin<Project> {

	override fun apply(project: Project) {
		project.tasks.withType<JavaCompile> {
			options.compilerArgs = options.compilerArgs + listOf(
				// Enable all warnings during compilation.
				"-Xlint:all",
				// Workaround for https://github.com/cashapp/paparazzi/issues/362.
				"-Xlint:-classfile",
				// > No processor claimed any of these annotations:
				// > javax.inject.Inject,
				// > androidx.annotation.Nullable,
				// > androidx.annotation.RequiresPermission,
				// > androidx.annotation.NonNull,
				// > dagger.Component,android.annotation.TargetApi
				"-Xlint:-processing",
				// Fail build when warnings pop up.
				"-Werror"
			)
		}
		project.tasks.withType<KotlinCompile> {
			kotlinOptions {
				allWarningsAsErrors = true
			}
		}
		project.tasks.withType<Test>().configureEach {
			ignoreFailures = isCI
		}
	}
}
