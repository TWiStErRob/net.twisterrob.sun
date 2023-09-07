plugins {
	id("project-module-java-library")
}

dependencies {
	api(libs.test.paparazzi)
	api(libs.test.junit4)
	api(libs.test.mockito)
	api(libs.test.paramInjector)
}
