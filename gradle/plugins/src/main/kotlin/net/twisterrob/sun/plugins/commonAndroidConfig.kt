package net.twisterrob.sun.plugins

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import org.gradle.api.Project
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

internal fun Project.commonAndroidConfig() {
	commonJavaConfig()
	// TODO https://github.com/gradle/android-cache-fix-gradle-plugin/issues/215
	//apply(plugin = "org.gradle.android.cache-fix")
	extensions.configure<BaseExtension> {
		compileSdkVersion(libs.versions.compileSdkVersion.get().toInt())
		defaultConfig {
			minSdk = libs.versions.minSdkVersion.get().toInt()
			testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
		lintOptions {
			xmlReport = true
			val projectSlug = project.path.substringAfter(':').replace(":", "+")
			baseline(rootProject.file("config/lint/baseline/lint_baseline-${projectSlug}.xml"))
		}

		// TODEL https://issuetracker.google.com/issues/211012777
		project.tasks.withType<AndroidLintTask>().configureEach {
			this.inputs.files(rootProject.file("lint.xml")).withPathSensitivity(PathSensitivity.RELATIVE)
			this.inputs.files(project.file("lint.xml")).withPathSensitivity(PathSensitivity.RELATIVE)
		}
	}
}
