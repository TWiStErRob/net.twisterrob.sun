import groovy.json.JsonOutput.toJson
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly

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

includeBuild("gradle/plugins")

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}

pluginManagement {
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
				includeGroup("com.gradle.enterprise")
				includeGroup("gradle.plugin.org.gradle.android")
				includeGroup("org.jetbrains.kotlin.android")
				includeGroup("io.gitlab.arturbosch.detekt")
			}
		}
		maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
			name = "Sonatype 01: SNAPSHOTs"
			content {
				includeVersionByRegex("""^net\.twisterrob\.gradle$""", ".*", """.*-SNAPSHOT$""")
				includeVersionByRegex("""^net\.twisterrob\.gradle$""", ".*", """.*-\d{8}\.\d{6}-\d+$""")
			}
			mavenContent {
				// This doesn't allow using specific snapshot, so using versionRegex above.
				//snapshotsOnly()
			}
		}
	}
	resolutionStrategy {
		// Not possible here, see root build.gradle.
		//cacheChangingModulesFor(0, "seconds") // -SNAPSHOT

		eachPlugin {
			// REPORT requested.version is null when using plugins {} block just above on Gradle 6.9.1.
			when (requested.id.id) {
				"net.twisterrob.settings" -> {
					useModule("net.twisterrob.gradle:twister-convention-settings:${requested.version}")
				}
				"net.twisterrob.root",
				"net.twisterrob.vcs",
				"net.twisterrob.java",
				"net.twisterrob.java-library",
				"net.twisterrob.kotlin",
				"net.twisterrob.android-app",
				"net.twisterrob.android-lib",
				"net.twisterrob.android-test" -> {
					useModule("net.twisterrob.gradle:twister-convention-plugins:${requested.version}")
				}
				"net.twisterrob.quality" -> {
					useModule("net.twisterrob.gradle:twister-quality:${requested.version}")
				}
			}
		}
	}
}

plugins {
	id("com.gradle.enterprise") version "3.11.4"
	id("net.twisterrob.settings") version "0.15-SNAPSHOT"
}

gradleEnterprise {
	buildScan {
		termsOfServiceUrl = "https://gradle.com/terms-of-service"
		termsOfServiceAgree = "yes"
		// TODO how to use net.twisterrob.sun.plugins.isCI? 
		if (System.getenv("GITHUB_ACTIONS") == "true") {
			fun setOutput(name: String, value: Any?) {
				// Using `appendText` to make sure out outputs are not cleared.
				// Using `\n` to make sure further outputs are correct.
				// Using `toJson()` to ensure that any special characters (such as newlines) are escaped.
				File(System.getenv("GITHUB_OUTPUT")).appendText("${name}=${toJson(value)}\n")
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
