plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
}

dependencies {
	api(project(":component:lib"))
	implementation(project(":component:states"))
	implementation(project(":component:theme"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)

	testImplementation(Deps.Test.junit4)
	testImplementation(Deps.Test.truth)
	testImplementation(Deps.Test.mockito)
	testImplementation(Deps.Test.paramInjector)
	testRuntimeOnly(project(":feature:configuration"))
}
