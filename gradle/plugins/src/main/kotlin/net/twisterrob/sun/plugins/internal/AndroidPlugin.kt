package net.twisterrob.sun.plugins.internal

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AndroidPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.android {
			namespace = "net.twisterrob.sun.${project.name}"
			compileSdk = project.libs.versions.compileSdkVersion.get().toInt()
			defaultConfig {
				minSdk = project.libs.versions.minSdkVersion.get().toInt()
				testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
			}
			lint {
				baseline = project.rootProject
					.file("config/lint/baseline/lint_baseline-${project.slug}.xml")
				lintConfig = project.rootProject
					.file("config/lint/lint.xml")
			}
		}
	}
}

private val Project.slug: String
	get() = path.substringAfter(':').replace(":", "+")
