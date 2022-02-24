plugins {
	id("project-module-android-library")
	id("project-dependencies")
	id("project-feature-paparazzi")
}

dependencies {
	implementation(projects.component.widget)
	implementation(projects.component.theme)
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)
	implementation(Deps.AndroidX.constraint)
	implementation(Deps.AndroidX.card)

	testImplementation(Deps.Test.junit4)
	testImplementation(Deps.Test.truth)
	testImplementation(Deps.Test.mockito)
	testImplementation(Deps.Test.paramInjector)
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
