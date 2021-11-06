plugins {
	id("net.twisterrob.java")
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
