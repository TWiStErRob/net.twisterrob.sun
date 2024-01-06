plugins {
	id("project-module-android-library")
}

dependencies {
	api(libs.test.paparazzi)
	api(libs.guava) {
		because("https://github.com/cashapp/paparazzi/issues/906")
	}

	api(libs.test.junit4)
	api(libs.test.mockito)
	api(libs.test.paramInjector)
}

// See https://github.com/cashapp/paparazzi/issues/1025#issuecomment-1687437843.
android.compileSdk = 33
