package net.twisterrob.sun.plugins.internal

import dev.detekt.gradle.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType

internal class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.plugins.apply("dev.detekt")
		project.dependencies.apply {
			// dev.detekt.gradle.plugin.CONFIGURATION_DETEKT_PLUGINS
			add("detektPlugins", project.libs.kotlin.detektLibraries)
		}
		project.detekt {
			// TODEL https://github.com/detekt/detekt/issues/4926
			buildUponDefaultConfig = false
			allRules = true
			//debug = true
			val rootDir = project.isolated.rootProject.projectDirectory
			config.setFrom(rootDir.file("config/detekt/detekt.yml"))
			baseline = rootDir.file("config/detekt/detekt-baseline-${project.name}.xml")
			basePath = rootDir

			parallel = true

			project.tasks.withType<Detekt>().configureEach {
				reports {
					html.required = true // human
					markdown.required = true // console
				}
			}
		}
	}
}
