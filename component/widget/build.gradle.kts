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
}
