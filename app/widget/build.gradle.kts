plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(project(":lib"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)
}
