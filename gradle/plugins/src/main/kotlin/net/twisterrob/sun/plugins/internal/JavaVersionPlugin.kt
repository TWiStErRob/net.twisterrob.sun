package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs.VersionAccessors
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal class JavaVersionPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val javaVersion = project.libs.versions.javaVersion
		// TODEL this is coming from my plugin, it forces sourceCompatibility to 7
		project.afterEvaluate {
			project.tasks.withType<JavaCompile>().configureEach {
				sourceCompatibility = javaVersion.toString()
				targetCompatibility = javaVersion.toString()
			}
		}
		project.tasks.withType<KotlinCompile>().configureEach {
			kotlinOptions {
				jvmTarget = javaVersion.toString()
			}
		}
		project.tasks.withType<Detekt>().configureEach {
			// Target version of the generated JVM bytecode. It is used for type resolution.
			jvmTarget = javaVersion.toString()
		}
	}
}

private val VersionAccessors.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(java.get())
