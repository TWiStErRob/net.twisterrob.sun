package net.twisterrob.sun.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class JavaLibraryPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.apply(plugin = "net.twisterrob.java-library")
		target.apply(plugin = "net.twisterrob.kotlin")
		target.commonJavaConfig()
	}
}
