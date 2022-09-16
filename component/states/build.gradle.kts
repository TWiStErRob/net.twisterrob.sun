plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(projects.component.core)
	implementation(projects.component.lib)
}
