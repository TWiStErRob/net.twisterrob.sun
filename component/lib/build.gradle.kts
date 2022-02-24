plugins {
	id("project-module-java-library")
	id("project-dependencies")
}

dependencies {
	implementation(projects.component.core)

	testImplementation(Deps.Test.junit4)
}
