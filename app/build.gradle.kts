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
	defaultConfig {
		applicationId = "net.twisterrob.sun"
		targetSdk = libs.versions.targetSdkVersion.get().toInt()
	}
}
