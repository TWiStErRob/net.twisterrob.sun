package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs.VersionAccessors
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal class JavaVersionPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val javaVersion = project.libs.versions.javaVersion
		// Assumes one of org.jetbrains.kotlin.jvm or org.jetbrains.kotlin.android is applied.
		project.extensions.configure<KotlinProjectExtension>("kotlin") {
			jvmToolchain(project.libs.versions.java.map(String::toInt).get())
		}
		project.tasks.withType<Detekt>().configureEach {
			// Target version of the generated JVM bytecode. It is used for type resolution.
			// TODO cannot use jvmTarget = javaVersion.toString() at the moment, because detekt 1.x is outdated.
			jvmTarget = JavaVersion.VERSION_22.toString()
		}
	}
}

private val VersionAccessors.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(this.java.get())
