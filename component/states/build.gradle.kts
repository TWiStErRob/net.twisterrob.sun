plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(Deps.AndroidX.annotations)
	implementation(project(":component:core"))
	implementation(project(":component:lib"))
}
