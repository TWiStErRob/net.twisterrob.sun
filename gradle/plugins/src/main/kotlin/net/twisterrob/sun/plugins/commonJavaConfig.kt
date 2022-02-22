package net.twisterrob.sun.plugins

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withType

internal fun Project.commonJavaConfig() {
	tasks.withType<JavaCompile> {
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
	plugins.apply("io.gitlab.arturbosch.detekt")
	plugins.withId("io.gitlab.arturbosch.detekt") {
		val detekt = this@commonJavaConfig.extensions.getByName<DetektExtension>("detekt")
		detekt.apply {
			ignoreFailures = true
			buildUponDefaultConfig = true
			allRules = true
			config = rootProject.files("config/detekt/detekt.yml")
			baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
			basePath = rootProject.projectDir.parentFile.absolutePath

			parallel = true

			tasks.withType<Detekt>().configureEach {
			}
		}
	}
}
