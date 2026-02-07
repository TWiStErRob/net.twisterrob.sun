package net.twisterrob.sun.plugins.internal

import dev.detekt.gradle.Detekt
import org.gradle.accessors.dm.LibrariesForLibs.VersionAccessors
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal class JavaVersionPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val javaVersion = project.libs.versions.javaVersion
		// Assumes one of org.jetbrains.kotlin.jvm or org.jetbrains.kotlin.android is applied.
		project.extensions.configure<KotlinProjectExtension>("kotlin") {
			jvmToolchain(project.libs.versions.java.runtime.map(String::toInt).get())
		}
		project.tasks.withType<Detekt>().configureEach {
			// Target version of the generated JVM bytecode. It is used for type resolution.
			jvmTarget = javaVersion.toString()
		}
	}
}

private val VersionAccessors.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(this.java.runtime.get())
