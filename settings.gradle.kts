import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly
import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.sun.plugins.isCI
import java.util.UUID

rootProject.name = "Sun"

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

include(":app")
include(":feature:configuration")
include(":feature:preview")
include(":component:core")
include(":component:widget")
include(":component:states")
include(":component:lib")
include(":component:paparazzi")
include(":component:theme")

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}

pluginManagement {
	includeBuild("gradle/plugins")
	repositories {
		google {
			content {
				includeGroupByRegex("""^com\.android(\..*)?$""")
				includeGroupByRegex("""^com\.google\..*$""")
				includeGroupByRegex("""^androidx\..*$""")
			}
		}
		mavenCentral()
		gradlePluginPortal {
			content {
				includeGroup("com.gradle")
				includeGroup("com.gradle.develocity")
				includeGroup("gradle.plugin.org.gradle.android")
				includeGroup("org.jetbrains.kotlin.android")
				includeGroup("io.gitlab.arturbosch.detekt")
			}
		}
	}
}

plugins {
	id("com.gradle.develocity") version "3.19"
	id("net.twisterrob.gradle.plugin.nagging") version "0.17"
	id("project-settings")
}

develocity {
	buildScan {
		termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
		termsOfUseAgree = "yes"
		if (isCI) {
			fun setOutput(name: String, value: Any?) {
				// Using `appendText` to make sure out outputs are not cleared.
				// Using `\n` at the end to make sure further outputs are correct.
				val delimiter = UUID.randomUUID().toString()
				File(System.getenv("GITHUB_OUTPUT"))
					.appendText("${name}<<${delimiter}\n${value}\n${delimiter}\n")
			}

			buildScanPublished {
				setOutput("build-scan-url", buildScanUri.toASCIIString())
			}
			gradle.addBuildListener(object : BuildAdapter() {
				@Deprecated("Won't work with configuration caching.")
				override fun buildFinished(result: BuildResult) {
					setOutput("result-success", result.failure == null)
					setOutput("result-text", resultText(result))
				}

				private fun resultText(result: BuildResult): String =
				    "${result.action} ${resultText(result.failure)}"

				private fun resultText(ex: Throwable?): String =
					when (ex) {
						null ->
							"Successful"
						is org.gradle.internal.exceptions.LocationAwareException ->
							"Failed: ${ex.message}"
						else ->
							"Failed with ${ex}"
					}
			})
		}
	}
}

val gradleVersion: String = GradleVersion.current().version
