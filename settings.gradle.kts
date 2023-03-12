import groovy.json.JsonOutput.toJson
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly
import net.twisterrob.gradle.doNotNagAbout

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
	}
}

plugins {
	id("com.gradle.enterprise") version "3.12.4"
	id("net.twisterrob.gradle.plugin.settings") version "0.15.1"
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

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-301430, fixed in AS Giraffe.
if ((System.getProperty("idea.version") ?: "") < "2022.3") {
	@Suppress("MaxLineLength")
	doNotNagAbout(
		"The org.gradle.util.GUtil type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_7.html#org_gradle_util_reports_deprecations",
		"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
	)
} else {
	error("Android Studio version changed, please remove hack.")
}

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
)

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.util.SourceSetCachedFinder.createArtifactsMap"
)
