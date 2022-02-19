plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(project(":component:core"))
	api(Deps.AndroidX.appcompat)
}
