plugins {
	id("net.twisterrob.java-library")
	id("project-dependencies")
}

repositories {
	google()
	mavenCentral()
}

dependencies {
	implementation(Deps.AndroidX.annotations)

	testImplementation(Deps.Test.junit4)
}
