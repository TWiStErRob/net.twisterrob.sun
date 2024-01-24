plugins {
	id("project-module-android-library")
}

dependencies {
	api(libs.test.paparazzi)
	api(libs.test.junit4)
	api(libs.test.mockito)
	api(libs.test.paramInjector)

	constraints {
		api("com.google.guava:guava") {
			attributes {
				attribute(
					TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
					objects.named<TargetJvmEnvironment>(TargetJvmEnvironment.STANDARD_JVM)
				)
			}
			because(
				"LayoutLib and sdk-common depend on Guava's -jre published variant." +
					"See https://github.com/cashapp/paparazzi/issues/906 ."
			)
		}
	}
}

// See https://github.com/cashapp/paparazzi/issues/1025#issuecomment-1687437843.
android.compileSdk = 33
