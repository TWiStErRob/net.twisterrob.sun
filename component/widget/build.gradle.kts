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
}

tasks.withType<Test>().configureEach {
	// TODEL https://github.com/cashapp/paparazzi/issues/305
	maxHeapSize = "1G"
}
