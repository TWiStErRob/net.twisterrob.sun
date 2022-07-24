package net.twisterrob.sun.plugins.internal

import net.twisterrob.sun.plugins.DetektPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

internal fun Project.commonJavaConfig() {
	plugins.apply(DetektPlugin::class)
	plugins.apply(JavaVersionPlugin::class)
	plugins.apply(StrictCompilationPlugin::class)
}
