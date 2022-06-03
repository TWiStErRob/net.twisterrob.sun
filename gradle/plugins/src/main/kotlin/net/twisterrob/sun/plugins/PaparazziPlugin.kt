package net.twisterrob.sun.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

class PaparazziPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.apply(plugin = "app.cash.paparazzi")
		target.dependencies {
			"testImplementation"(target.project(":component:paparazzi"))
		}

		target.tasks.named<Delete>("clean") {
			delete(project.file("out/failures"))
		}

		target.tasks.withType<Test>().configureEach {
			useJUnit {
				if (project.property("net.twisterrob.build.screenshot-tests").toString().toBoolean()) {
					includeCategories(
						"net.twisterrob.sun.test.screenshots.ScreenshotTest"
					)
				} else {
					excludeCategories(
						"net.twisterrob.sun.test.screenshots.ScreenshotTest"
					)
				}
			}
		}
	}
}
