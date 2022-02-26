package net.twisterrob.sun.plugins

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.commonAndroidConfig() {
	commonJavaConfig()
	// TODO https://github.com/gradle/android-cache-fix-gradle-plugin/issues/215
	//apply(plugin = "org.gradle.android.cache-fix")
	@Suppress("UnstableApiUsage")
	extensions.configure<BaseExtension> {
		this as CommonExtension<*, *, *, *>
		compileSdk = libs.versions.compileSdkVersion.get().toInt()
		defaultConfig {
			minSdk = libs.versions.minSdkVersion.get().toInt()
			testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
		lint {
			xmlReport = true
			val projectSlug = project.path.substringAfter(':').replace(":", "+")
			baseline = rootProject.file("config/lint/baseline/lint_baseline-${projectSlug}.xml")
		}
	}
}
