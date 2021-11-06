plugins {
	id("project-module-android-app")
	id("project-dependencies")
}

dependencies {
	implementation(project(":app:widget"))
	implementation(project(":lib"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)
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
