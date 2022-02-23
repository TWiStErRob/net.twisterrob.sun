plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
}

dependencies {
	implementation(project(":component:core"))
	kapt(Deps.Dagger.compiler)
	api(project(":component:lib"))
	implementation(project(":component:states"))
	implementation(project(":component:theme"))
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)
}

dependencies {
	testRuntimeOnly(project(":feature:configuration"))
}

dependencies {
	androidTestImplementation(Deps.Test.junit4)

	androidTestImplementation("androidx.test:core:1.4.0")
	androidTestImplementation("androidx.test:runner:1.4.0")
}
