plugins {
	id("project-module-android-library")
	id("project-dependencies")
}

dependencies {
	implementation(project(":component:widget"))
	implementation(Deps.AndroidX.annotations)
	implementation(Deps.AndroidX.v4)
}

android {
	buildFeatures {
		buildConfig = true
	}
}
