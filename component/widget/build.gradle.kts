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

	testRuntimeOnly(project(":feature:configuration"))
}
