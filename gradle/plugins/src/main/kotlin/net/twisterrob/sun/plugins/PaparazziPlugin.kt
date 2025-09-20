package net.twisterrob.sun.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

public class PaparazziPlugin : Plugin<Project> {

	override fun apply(target: Project) {
		target.plugins.apply("app.cash.paparazzi")
		target.dependencies {
			"testImplementation"(target.project(":component:paparazzi"))
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
			// Mute logging, TODEL workaround for https://github.com/cashapp/paparazzi/issues/1842
			systemProperty("java.util.logging.config.class", "net.twisterrob.sun.test.screenshots.PaparazziLoggingConfiguration")
			// Hide the huge yellow WARNINGs:
			jvmArgs(
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

				// > An illegal reflective access operation has occurred
				// > Illegal reflective access by net.twisterrob.sun.test.screenshots.ReflectionKt
				// (file:/P:/projects/workspace/net.twisterrob.sun/component/paparazzi/build/libs/paparazzi.jar)
				// to method java.lang.Class.getDeclaredFields0(boolean)
				// > Please consider reporting this to the maintainers of net.twisterrob.sun.test.screenshots.ReflectionKt
				// > Use --illegal-access=warn to enable warnings of further illegal reflective access operations
				// > All illegal access operations will be denied in a future release
				"--add-opens", "java.base/java.lang=ALL-UNNAMED",
			)
		}
	}
}
