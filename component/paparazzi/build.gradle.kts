plugins {
	id("project-module-android-library")
}

dependencies {
	api(libs.test.paparazzi)
	api(libs.test.junit4)
	api(libs.test.mockito)
	api(libs.test.paramInjector)
}

kotlin {
	compilerOptions {
		// More focused version of -Xdont-warn-on-error-suppression, except:
		// > e: Diagnostic "NOTHING_TO_OVERRIDE" is an error. Global suppression of errors is prohibited
		//freeCompilerArgs.add("-Xsuppress-warning=NOTHING_TO_OVERRIDE")
		// TODO try to update this after 2.2.0 according to https://youtrack.jetbrains.com/issue/KT-73606#focus=Comments-27-11596919.0-0
		freeCompilerArgs.add("-Xdont-warn-on-error-suppression")
	}
}
