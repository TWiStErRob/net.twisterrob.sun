plugins {
	id("project-module-android-library")
	id("project-dependencies")
}

dependencies {
	implementation(projects.component.widget)
	implementation(projects.component.theme)
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)
}
