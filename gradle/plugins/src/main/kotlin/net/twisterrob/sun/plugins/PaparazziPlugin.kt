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
				// (file:/.../component/paparazzi/build/libs/paparazzi.jar)
				// to field java.lang.reflect.Field.modifiers
				// > Illegal reflective access by LocationPermissionCompatTest
				// (.../feature/configuration/build/intermediates/javac/debugUnitTest/classes/)
				// to field java.lang.reflect.Field.modifiers
				// > Please consider reporting this to the maintainers of net.twisterrob.sun.test.screenshots.ClearFinalKt
				// > Use --illegal-access=warn to enable warnings of further illegal reflective access operations
				// > All illegal access operations will be denied in a future release
				"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",

				// > An illegal reflective access operation has occurred
				// > Illegal reflective access by net.twisterrob.sun.test.screenshots.ReflectionKt
				// (file:/.../component/paparazzi/build/libs/paparazzi.jar)
				// to method java.lang.Class.getDeclaredFields0(boolean)
				// > Please consider reporting this to the maintainers of net.twisterrob.sun.test.screenshots.ReflectionKt
				// > Use --illegal-access=warn to enable warnings of further illegal reflective access operations
				// > All illegal access operations will be denied in a future release
				"--add-opens=java.base/java.lang=ALL-UNNAMED",

				// > A restricted method in java.lang.System has been called
				// > java.lang.System::load has been called by com.android.layoutlib.bridge.Bridge in an unnamed module (file:/GRADLE_USER_HOME/caches/9.2.1/transforms/.../workspace/transformed/layoutlib-15.2.3.jar)
				// > Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
				// > Restricted methods will be blocked in a future release unless native access is enabled
				"--enable-native-access=ALL-UNNAMED",

				// > A terminally deprecated method in sun.misc.Unsafe has been called
				// > sun.misc.Unsafe::staticFieldOffset has been called by app.cash.paparazzi.ReflectionsKt (file:/GRADLE_USER_HOME/caches/9.2.1/transforms/.../workspace/transformed/paparazzi-2.0.0-alpha02.jar)
				// > Please consider reporting this to the maintainers of class app.cash.paparazzi.ReflectionsKt
				// > sun.misc.Unsafe::staticFieldOffset will be removed in a future release
				"--sun-misc-unsafe-memory-access=allow",
			)
		}
	}
}
