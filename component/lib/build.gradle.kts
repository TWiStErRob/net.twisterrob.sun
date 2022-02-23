plugins {
	id("project-module-java-library")
	id("project-dependencies")
}

dependencies {
	implementation(project(":component:core"))

	testImplementation(Deps.Test.junit4)
}
