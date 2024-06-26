@file:Suppress("INVISIBLE_REFERENCE") // https://youtrack.jetbrains.com/issue/KT-68935

package net.twisterrob.sun.plugins.internal

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.utils.forAllTargets

internal class StrictCompilationPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.tasks.withType<JavaCompile>().configureEach {
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
		@Suppress("INVISIBLE_MEMBER") // https://youtrack.jetbrains.com/issue/KT-68935
		project.kotlinExtension.forAllTargets { target ->
			target.compilations.configureEach {
				compileTaskProvider.configure {
					compilerOptions {
						verbose = true
						allWarningsAsErrors = true
					}
				}
			}
		}
	}
}
