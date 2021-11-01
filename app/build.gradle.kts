plugins {
	id("net.twisterrob.android-app")
}

dependencies {
	implementation(project(":lib"))
	implementation("com.android.support:support-annotations:23.2.0")
	implementation("com.android.support:support-v4:23.2.0")
}

android {
	// When changing this, update CI too (platforms;android-xx).
	compileSdkVersion(23)
	defaultConfig {
		applicationId = "net.twisterrob.sun"
		minSdkVersion(10)
		targetSdkVersion(19) // TODEL ExpiredTargetSdkVersion when updating
	}
	lintOptions {
		disable("ExpiredTargetSdkVersion")
	}
}
