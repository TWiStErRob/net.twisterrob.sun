plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
}

// We set up group and version to make this project available as dependency artifact.
group = "project-dependencies"
version = "SNAPSHOT"

repositories {
	mavenCentral()
}

gradlePlugin {
	plugins.register("project-dependencies") {
		id = "project-dependencies"
		implementationClass = "net.twisterrob.sun.dependencies.DependenciesPlugin"
	}
}

kotlinDslPluginOptions {
	experimentalWarning.set(false)
}
