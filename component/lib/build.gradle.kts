plugins {
	id("project-module-java-library")
	id("project-dependencies")
}

dependencies {
	implementation(Deps.AndroidX.annotations)

	testImplementation(Deps.Test.junit4)
}
