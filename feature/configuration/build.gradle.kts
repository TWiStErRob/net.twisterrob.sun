plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
}

dependencies {
	implementation(projects.component.widget)
	implementation(projects.component.theme)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.fragment)
	implementation(libs.androidx.constraint)
	implementation(libs.androidx.card)

	testImplementation(libs.test.junit4)
	testImplementation(libs.test.truth)
	testImplementation(libs.test.mockito)
	testImplementation(libs.test.paramInjector)
}

android {
	buildFeatures {
		buildConfig = true
	}
	namespace = "net.twisterrob.sun.configuration"
}
