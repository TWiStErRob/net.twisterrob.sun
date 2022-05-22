package net.twisterrob.sun.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class RootPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		// Dummy plugin, does nothing, except make all the classes available to the build.gradle.kts.
	}
}
