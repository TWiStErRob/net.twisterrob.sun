plugins {
	id("project-module-android-library")
	id("project-dependencies")
}

dependencies {
	implementation(project(":component:widget"))
	implementation(project(":component:theme"))
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)
}
