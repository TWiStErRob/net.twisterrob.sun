plugins {
	id("project-module-android-library")
	id("project-dependencies")
	id("project-feature-paparazzi")
}

dependencies {
	implementation(project(":component:widget"))
	implementation(project(":component:theme"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)
}

android {
	buildFeatures {
		buildConfig = true
	}
}
