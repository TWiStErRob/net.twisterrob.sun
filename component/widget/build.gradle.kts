plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
	id("com.google.devtools.ksp")
}

dependencies {
	implementation(projects.component.core)
	ksp(libs.dagger.compiler)
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

	androidTestImplementation(libs.test.androidxCore)
	androidTestImplementation(libs.test.androidxRunner)
}
