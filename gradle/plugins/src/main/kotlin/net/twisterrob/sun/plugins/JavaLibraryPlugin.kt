package net.twisterrob.sun.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.repositories

class JavaLibraryPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.apply(plugin = "net.twisterrob.java-library")
		target.repositories {
			google()
			mavenCentral()
		}
		target.commonJavaConfig()
	}
}
