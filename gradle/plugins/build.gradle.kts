plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	id("project-dependencies")
}

repositories {
	google()
	mavenCentral()
}

dependencies {
	implementation(Deps.Android.plugin)
}

gradlePlugin {
	plugins.register("project-module-android-library") {
		id = "project-module-android-library"
		implementationClass = "net.twisterrob.sun.plugins.AndroidLibraryPlugin"
	}
	plugins.register("project-module-android-app") {
		id = "project-module-android-app"
		implementationClass = "net.twisterrob.sun.plugins.AndroidAppPlugin"
	}
}

kotlinDslPluginOptions {
	experimentalWarning.set(false)
}
