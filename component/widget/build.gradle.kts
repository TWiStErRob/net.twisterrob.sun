plugins {
	id("project-module-android-library")
}

dependencies {
	api(project(":component:lib"))
	implementation(project(":component:states"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)
}
