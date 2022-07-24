package net.twisterrob.sun.plugins.internal

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class JavaVersionPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val javaVersion = project.libs.versions.javaVersion
		project.tasks.withType<JavaCompile> {
			sourceCompatibility = javaVersion.toString()
			targetCompatibility = javaVersion.toString()
		}
		project.tasks.withType<KotlinCompile> {
			kotlinOptions {
				jvmTarget = javaVersion.toString()
			}
		}
	}
}
