package net.twisterrob.sun.plugins

import net.twisterrob.sun.plugins.internal.AndroidLintSarifMergePlugin
import net.twisterrob.sun.plugins.internal.AndroidPlugin
import net.twisterrob.sun.plugins.internal.DetektPlugin
import net.twisterrob.sun.plugins.internal.DetektReportMergePlugin
import net.twisterrob.sun.plugins.internal.JavaVersionPlugin
import net.twisterrob.sun.plugins.internal.StrictCompilationPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class RootPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		// Dummy plugin, does nothing, except make all the classes available to the build.gradle.kts.
	}
}

class JavaLibraryPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.apply(plugin = "net.twisterrob.java-library")
		target.apply(plugin = "net.twisterrob.kotlin")
		commonJavaPlugins(target)
	}
}

class AndroidAppPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.apply(plugin = "net.twisterrob.android-app")
		target.apply(plugin = "net.twisterrob.kotlin")
		commonJavaPlugins(target)
		commonAndroidPlugins(target)
	}
}

class AndroidLibraryPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.apply(plugin = "net.twisterrob.android-library")
		target.apply(plugin = "net.twisterrob.kotlin")
		commonJavaPlugins(target)
		commonAndroidPlugins(target)
	}
}

private fun commonJavaPlugins(target: Project) {
	target.plugins.apply(DetektPlugin::class)
	target.plugins.apply(DetektReportMergePlugin::class)
	target.plugins.apply(JavaVersionPlugin::class)
	target.plugins.apply(StrictCompilationPlugin::class)
}

private fun commonAndroidPlugins(target: Project) {
	// TODO https://github.com/gradle/android-cache-fix-gradle-plugin/issues/215
	//apply(plugin = "org.gradle.android.cache-fix")
	target.plugins.apply(AndroidPlugin::class)
	target.plugins.apply(AndroidLintSarifMergePlugin::class)
}
