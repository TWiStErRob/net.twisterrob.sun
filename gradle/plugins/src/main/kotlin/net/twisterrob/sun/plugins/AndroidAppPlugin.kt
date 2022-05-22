package net.twisterrob.sun.plugins

import net.twisterrob.sun.plugins.internal.commonAndroidConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class AndroidAppPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.apply(plugin = "net.twisterrob.android-app")
		target.apply(plugin = "net.twisterrob.kotlin")
		target.commonAndroidConfig()
	}
}
