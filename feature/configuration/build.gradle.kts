plugins {
	id("project-module-android-library")
	id("project-dependencies")
	id("project-feature-paparazzi")
}

dependencies {
	implementation(project(":component:widget"))
	implementation(project(":component:theme"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)
	implementation(Deps.ThirdParty.permissions)
	implementation(Deps.AndroidX.constraint)

	testImplementation(Deps.Test.junit4)
	testImplementation(Deps.Test.mockito)
	testImplementation(Deps.Test.paramInjector)
}

android {
	buildFeatures {
		buildConfig = true
	}
}
