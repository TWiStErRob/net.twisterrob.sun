plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
}

dependencies {
	implementation(projects.component.core)
	kapt(Deps.Dagger.compiler)
	api(projects.component.lib)
	implementation(projects.component.states)
	implementation(projects.component.theme)
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)
}

dependencies {
	testRuntimeOnly(projects.feature.configuration)
}

dependencies {
	androidTestImplementation(Deps.Test.junit4)

	androidTestImplementation(Deps.Test.androidxCore)
	androidTestImplementation(Deps.Test.androidxRunner)
}
