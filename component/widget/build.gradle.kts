plugins {
	id("project-module-android-library")
	id("app.cash.paparazzi") version "0.8.0"
}

dependencies {
	api(project(":component:lib"))
	implementation(project(":component:states"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)

	testImplementation(Deps.Test.junit4)
	testImplementation(Deps.Test.mockito)
	testImplementation(Deps.Test.paramInjector)
	testImplementation(project(":feature:configuration"))
	// TODEL https://github.com/cashapp/paparazzi/pull/308
	testImplementation(platform(Deps.Kotlin.bom))
	// TODEL https://github.com/cashapp/paparazzi/issues/306
	testCompileOnly(project(":component:awt-hack"))
}

tasks.withType<Test>().configureEach {
	// TODEL https://github.com/cashapp/paparazzi/issues/305
	maxHeapSize = "1G"

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

tasks.named<Delete>("clean") {
	delete(project.file("out/failures"))
}
