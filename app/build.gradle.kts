plugins {
	id("project-module-android-app")
	id("project-dependencies")
}

dependencies {
	implementation(project(":app:configuration"))
	implementation(project(":app:widget"))
	debugImplementation(project(":app:preview"))
}

android {
	defaultConfig {
		applicationId = "net.twisterrob.sun"
		targetSdkVersion(19) // TODEL ExpiredTargetSdkVersion when updating
	}
	lintOptions {
		disable("ExpiredTargetSdkVersion")
	}
}
