plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
}

dependencies {
	api(project(":component:lib"))
	implementation(project(":component:states"))
	implementation(project(":component:theme"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)

	testImplementation(project(":feature:configuration"))
}
