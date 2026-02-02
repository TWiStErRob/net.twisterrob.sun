package net.twisterrob.sun.plugins.internal

import org.gradle.api.DomainObjectCollection
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

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
				// > build\generated\source\buildConfig\debug\...\BuildConfig.java:1:
				// > warning: [dangling-doc-comments]
				// > documentation comment is not attached to any declaration
				"-Xlint:-dangling-doc-comments",
				// Fail build when warnings pop up.
				"-Werror"
			)
		}
		project.kotlinExtension.targets.configureEach {
			compilations.configureEach {
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

/**
 * Copy of `import org.jetbrains.kotlin.gradle.utils.targets` with adjustments.
 * See https://youtrack.jetbrains.com/issue/KT-68935.
 * @see org.jetbrains.kotlin.gradle.utils.targets
 */
private val KotlinProjectExtension.targets: DomainObjectCollection<KotlinTarget>
	get() = when (this) {
		is KotlinMultiplatformExtension -> targets
		is KotlinSingleTargetExtension<*> -> project.objects.domainSetOf(target)
		else -> error("Unexpected 'kotlin' extension ${this}")
	}

private inline fun <reified T : Any> ObjectFactory.domainSetOf(vararg elements: T): DomainObjectSet<T> =
	this
		.domainObjectSet(T::class.java)
		.apply { addAll(elements.asList()) }
