package net.twisterrob.sun.plugins

import net.twisterrob.sun.plugins.internal.AndroidLintSarifMergePlugin
import net.twisterrob.sun.plugins.internal.AndroidPlugin
import net.twisterrob.sun.plugins.internal.DetektPlugin
import net.twisterrob.sun.plugins.internal.DetektReportMergePlugin
import net.twisterrob.sun.plugins.internal.GitHubActionsPlugin
import net.twisterrob.sun.plugins.internal.JavaVersionPlugin
import net.twisterrob.sun.plugins.internal.StrictCompilationPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply

public class SettingsPlugin : Plugin<Settings> {
	override fun apply(target: Settings) {
		// Dummy plugin, does nothing, except make all the classes available to the settings.gradle.kts.
	}
}

public class RootPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		// Dummy plugin, does nothing, except make all the classes available to the build.gradle.kts.
	}
}

public class JavaLibraryPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.plugins.apply("net.twisterrob.gradle.plugin.java-library")
		target.plugins.apply("org.jetbrains.kotlin.jvm")
		commonJavaPlugins(target)
	}
}

public class AndroidAppPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.plugins.apply("net.twisterrob.gradle.plugin.android-app")
		target.plugins.apply("org.jetbrains.kotlin.android")
		commonJavaPlugins(target)
		commonAndroidPlugins(target)
	}
}

public class AndroidLibraryPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.plugins.apply("net.twisterrob.gradle.plugin.android-library")
		target.plugins.apply("org.jetbrains.kotlin.android")
		commonJavaPlugins(target)
		commonAndroidPlugins(target)
	}
}

private fun commonJavaPlugins(target: Project) {
	target.plugins.apply(DetektPlugin::class)
	target.plugins.apply(DetektReportMergePlugin::class)
	target.plugins.apply(JavaVersionPlugin::class)
	target.plugins.apply(StrictCompilationPlugin::class)
	target.plugins.apply(GitHubActionsPlugin::class)
}

private fun commonAndroidPlugins(target: Project) {
	// TODO https://github.com/gradle/android-cache-fix-gradle-plugin/issues/215
	//apply(plugin = "org.gradle.android.cache-fix")
	target.plugins.apply(AndroidPlugin::class)
	target.plugins.apply(AndroidLintSarifMergePlugin::class)
}
