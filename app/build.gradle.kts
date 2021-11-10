plugins {
	id("project-module-android-app")
	id("project-dependencies")
}

dependencies {
	implementation(project(":feature:configuration"))
	implementation(project(":component:widget"))
	implementation(project(":component:theme"))
	debugImplementation(project(":feature:preview"))
}

android {
	defaultConfig {
		applicationId = "net.twisterrob.sun"
		targetSdkVersion(Deps.Android.targetSdkVersion)
	}
}
