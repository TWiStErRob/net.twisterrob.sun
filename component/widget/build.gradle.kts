plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(projects.component.core)
	kapt(libs.dagger.compiler)
	api(projects.component.lib)
	implementation(projects.component.states)
	implementation(projects.component.theme)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.fragment)
}

dependencies {
	testRuntimeOnly(projects.feature.configuration)
}

dependencies {
	androidTestImplementation(libs.test.junit4)

	androidTestImplementation(libs.test.androidx.core)
	androidTestImplementation(libs.test.androidx.runner)
}
