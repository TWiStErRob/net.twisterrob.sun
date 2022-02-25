plugins {
	id("project-module-android-library")
	id("project-feature-paparazzi")
}

dependencies {
	implementation(projects.component.widget)
	implementation(projects.component.theme)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.fragment)
	implementation(libs.androidx.constraint)
	implementation(libs.androidx.card)

	testImplementation(libs.test.junit4)
	testImplementation(libs.test.truth)
	testImplementation(libs.test.mockito)
	testImplementation(libs.test.paramInjector)
}

android {
	buildFeatures {
		buildConfig = true
	}
}

tasks.withType<Test>().configureEach {
	jvmArgs(
		// Hide the huge yellow WARNING:
		// > An illegal reflective access operation has occurred
		// > Illegal reflective access by LocationPermissionCompatTest
		// (.../feature/configuration/build/intermediates/javac/debugUnitTest/classes/)
		// to field java.lang.reflect.Field.modifiers
		// > Please consider reporting this to the maintainers of LocationPermissionCompatTest
		// > Use --illegal-access=warn to enable warnings of further illegal reflective access operations
		// > All illegal access operations will be denied in a future release
		"--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
	)
}
