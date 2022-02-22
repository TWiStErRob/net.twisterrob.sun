plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	id("project-dependencies")
}

repositories {
	google()
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation(Deps.Android.plugin)
	implementation(Deps.Android.cacheFix)
	implementation(Deps.Kotlin.detekt)
	implementation("project-dependencies:dependencies:SNAPSHOT")
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
	plugins.register("project-module-java-library") {
		id = "project-module-java-library"
		implementationClass = "net.twisterrob.sun.plugins.JavaLibraryPlugin"
	}
	plugins.register("project-feature-paparazzi") {
		id = "project-feature-paparazzi"
		implementationClass = "net.twisterrob.sun.plugins.PaparazziPlugin"
	}
}
