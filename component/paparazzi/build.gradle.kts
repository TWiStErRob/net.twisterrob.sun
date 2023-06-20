plugins {
	id("project-module-java-library")
}

dependencies {
	api(libs.test.paparazzi)
	api(libs.guava.jre) {
		because(
			"Help Gradle select the Guava -jre flavor instead of -android. "
				+ "See https://github.com/cashapp/paparazzi/issues/906."
		)
	}
	api(libs.test.junit4)
	api(libs.test.mockito)
	api(libs.test.paramInjector)
}
