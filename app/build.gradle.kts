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
		targetSdkVersion(Deps.Android.targetSdkVersion)
	}
	lintOptions {
		disable("ExpiredTargetSdkVersion")
	}
}
