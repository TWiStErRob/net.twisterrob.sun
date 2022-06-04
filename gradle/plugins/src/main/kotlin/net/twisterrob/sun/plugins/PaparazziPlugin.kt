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

		// TODEL workaround for https://github.com/cashapp/paparazzi/issues/446
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
			jvmArgs(
				// Hide the huge yellow WARNING:
				// > An illegal reflective access operation has occurred
				// > Illegal reflective access by net.twisterrob.sun.test.screenshots.ClearFinalKt
				// (file:/P:/projects/workspace/net.twisterrob.sun/component/paparazzi/build/libs/paparazzi.jar)
				// to field java.lang.reflect.Field.modifiers
				// > Illegal reflective access by LocationPermissionCompatTest
				// (.../feature/configuration/build/intermediates/javac/debugUnitTest/classes/)
				// to field java.lang.reflect.Field.modifiers
				// > Please consider reporting this to the maintainers of net.twisterrob.sun.test.screenshots.ClearFinalKt
				// > Use --illegal-access=warn to enable warnings of further illegal reflective access operations
				// > All illegal access operations will be denied in a future release
				"--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
			)
		}
	}
}
