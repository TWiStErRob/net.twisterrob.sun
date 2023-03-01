plugins {
	`kotlin-dsl` // id("org.gradle.kotlin.kotlin-dsl") but with automatic appliedKotlinDslPluginsVersion.
	id("java-gradle-plugin")
}

dependencies {
	implementation(libs.android.gradle)
	// To have access to com.android.utils.SdkUtils.
	implementation(libs.android.lint.common)
	implementation(libs.android.cacheFix)
	implementation(libs.kotlin.plugin)
	implementation(libs.kotlin.detekt)
	implementation(libs.kotlin.detekt.sarif)
	implementation(libs.twisterrob.quality)
	implementation(libs.twisterrob.convention)

	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

	testImplementation(libs.test.junit4)
	testImplementation(libs.test.jsonAssert)
}

kotlin {
	explicitApi()
	target.compilations.configureEach {
		kotlinOptions {
			verbose = true
			allWarningsAsErrors = true
		}
	}
}

gradlePlugin {
	plugins.register("project-module-root") {
		id = "project-module-root"
		implementationClass = "net.twisterrob.sun.plugins.RootPlugin"
	}
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
