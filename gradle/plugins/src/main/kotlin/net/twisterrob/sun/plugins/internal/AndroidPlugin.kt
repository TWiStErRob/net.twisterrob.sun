package net.twisterrob.sun.plugins.internal

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

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
			testOptions {
				unitTests {
					all { task: Test ->
						// Hide (https://stackoverflow.com/a/79098701/253468)
						// > Java HotSpot(TM) 64-Bit Server VM warning:
						// > Sharing is only supported for boot loader classes because bootstrap classpath has been appended
						task.jvmArgs("-Xshare:off")
					}
				}
			}
		}
	}
}

private val Project.slug: String
	get() = path.substringAfter(':').replace(":", "+")
