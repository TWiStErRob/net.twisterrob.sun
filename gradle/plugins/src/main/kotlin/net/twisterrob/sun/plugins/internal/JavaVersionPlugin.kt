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
		project.androidOptional {
			// TODEL Explicitly specify Java version because https://issuetracker.google.com/issues/260059413
			// > > Task :app:compileDebugKotlin
			// > 'compileDebugJavaWithJavac' task (current target is 1.8) and
			// > 'compileDebugKotlin' task (current target is 11)
			// > jvm target compatibility should be set to the same Java version.
			// > By default will become an error since Gradle 8.0+!
			// > Read more: https://kotl.in/gradle/jvm/target-validation
			// > Consider using JVM toolchain: https://kotl.in/gradle/jvm/toolchain
			compileOptions {
				sourceCompatibility = javaVersion
				targetCompatibility = javaVersion
			}
		}
		project.tasks.withType<JavaCompile>().configureEach {
			sourceCompatibility = javaVersion.toString()
			targetCompatibility = javaVersion.toString()
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
	get() = JavaVersion.toVersion(this.java.get())
