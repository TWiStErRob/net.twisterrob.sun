plugins {
	id("project-module-android-library")
}

dependencies {
	api(project(":lib"))
	implementation(project(":app:states"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)
}
