plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(project(":component:core"))
	implementation(project(":component:lib"))
}
