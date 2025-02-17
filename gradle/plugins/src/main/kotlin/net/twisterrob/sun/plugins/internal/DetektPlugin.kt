package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

internal class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.plugins.apply("io.gitlab.arturbosch.detekt")
		project.dependencies.apply {
			add(CONFIGURATION_DETEKT_PLUGINS, project.libs.kotlin.detektLibraries)
		}
		project.detekt {
			// TODEL https://github.com/detekt/detekt/issues/4926
			buildUponDefaultConfig = false
			allRules = true
			//debug = true
			config.setFrom(project.rootProject.file("config/detekt/detekt.yml"))
			baseline = project.rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
			basePath = project.rootProject.projectDir.absolutePath

			parallel = true

			project.tasks.withType<Detekt>().configureEach {
				reports {
					html.required.set(true) // human
					txt.required.set(true) // console
				}
			}
		}
	}
}
