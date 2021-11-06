plugins {
	id("project-module-android-library")
	id("project-dependencies")
}

dependencies {
	implementation(project(":app:widget"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)
}
