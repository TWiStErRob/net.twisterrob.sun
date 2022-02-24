plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(projects.component.core)
	api(Deps.AndroidX.appcompat)
}
