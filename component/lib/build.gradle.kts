plugins {
	id("project-module-java-library")
}

dependencies {
	implementation(projects.component.core)

	testImplementation(libs.test.junit4)
}
