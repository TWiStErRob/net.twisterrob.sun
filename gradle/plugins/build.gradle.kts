plugins {
	id("org.gradle.kotlin.kotlin-dsl") version "2.1.7" // see appliedKotlinDslPluginsVersion
	id("java-gradle-plugin")
}

repositories {
	google()
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation(libs.android.gradle)
	implementation(libs.android.cacheFix)
	implementation(libs.kotlin.plugin)
	implementation(libs.kotlin.detekt)

	// TODEL hack from https://github.com/gradle/gradle/issues/15383#issuecomment-779893192 (there are more parts to this)
	implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
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
