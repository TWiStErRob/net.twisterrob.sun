plugins {
	id("project-module-java-library")
	id("project-dependencies")
}

dependencies {
	api(Deps.AndroidX.annotations)
	api(Deps.Test.paparazzi)
	api(Deps.Test.junit4)
	api(Deps.Test.mockito)
	api(Deps.Test.paramInjector)
	// TODEL https://github.com/cashapp/paparazzi/pull/308
	api(platform(Deps.Kotlin.bom))
	// TODEL https://github.com/cashapp/paparazzi/issues/306
	compileOnly(project(":component:awt-hack"))
}
