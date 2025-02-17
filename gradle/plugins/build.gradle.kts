import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
	`kotlin-dsl` // id("org.gradle.kotlin.kotlin-dsl") but with automatic appliedKotlinDslPluginsVersion.
	id("java-gradle-plugin")
}

dependencies {
	// To have access to com.android.utils.SdkUtils.
	implementation(libs.android.lintCommon)
	implementation(libs.kotlin.detektSarif)
	implementation(libs.plugins.android.app.asDependency())
	implementation(libs.plugins.android.cacheFix.asDependency())
	implementation(libs.plugins.android.lib.asDependency())
	implementation(libs.plugins.kotlin.android.asDependency())
	implementation(libs.plugins.kotlin.detekt.asDependency())
	implementation(libs.plugins.kotlin.jvm.asDependency())
	implementation(libs.plugins.kotlin.pluginKsp.asDependency())
	implementation(libs.plugins.paparazzi.asDependency())
	implementation(libs.plugins.twisterrob.androidApp.asDependency())
	implementation(libs.plugins.twisterrob.convention.asDependency())
	implementation(libs.plugins.twisterrob.quality.asDependency())

	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))

	testImplementation(libs.test.junit4)
	testImplementation(libs.test.jsonAssert)
}

kotlin {
	explicitApi = ExplicitApiMode.Strict
	compilerOptions {
		verbose = true
		allWarningsAsErrors = true
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
	plugins.register("project-settings") {
		id = "project-settings"
		implementationClass = "net.twisterrob.sun.plugins.SettingsPlugin"
	}
}

fun Provider<PluginDependency>.asDependency(): Provider<String> =
	this.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
