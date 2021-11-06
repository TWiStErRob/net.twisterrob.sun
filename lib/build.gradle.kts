plugins {
	id("net.twisterrob.java")
	id("project-dependencies")
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(Deps.Test.junit4)
}
