plugins {
	id("project-module-android-app")
}

dependencies {
	implementation(projects.component.core)
	implementation(projects.feature.configuration)
	implementation(projects.component.widget)
	implementation(projects.component.theme)
	debugImplementation(projects.feature.preview)
}

android {
	namespace = "net.twisterrob.sun.android"
	defaultConfig {
		applicationId = "net.twisterrob.sun"
		targetSdk = libs.versions.targetSdkVersion.get().toInt()
	}
	// TODEL AGP 7.2 https://issuetracker.google.com/issues/194525628
	if (System.getProperty("idea.is.internal").toBoolean()) {
		lint.checkReleaseBuilds = false
	}
}
