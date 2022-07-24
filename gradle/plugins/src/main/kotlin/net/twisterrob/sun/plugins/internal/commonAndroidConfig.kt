package net.twisterrob.sun.plugins.internal

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

internal fun Project.commonAndroidConfig() {
	commonJavaConfig()
	// TODO https://github.com/gradle/android-cache-fix-gradle-plugin/issues/215
	//apply(plugin = "org.gradle.android.cache-fix")
	plugins.apply(AndroidPlugin::class)
	plugins.apply(AndroidLintSarifMergePlugin::class)
}
