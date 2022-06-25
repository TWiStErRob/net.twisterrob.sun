package net.twisterrob.sun.plugins.internal

import net.twisterrob.sun.plugins.DetektPlugin
import net.twisterrob.sun.plugins.isCI
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.commonJavaConfig() {
	plugins.apply(DetektPlugin::class)
	tasks.withType<JavaCompile> {
		sourceCompatibility = libs.versions.javaVersion.toString()
		targetCompatibility = libs.versions.javaVersion.toString()
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
	tasks.withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = libs.versions.javaVersion.toString()
			allWarningsAsErrors = true
		}
	}
	tasks.withType<Test>().configureEach {
		ignoreFailures = isCI
	}
}
