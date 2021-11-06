plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
}

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
