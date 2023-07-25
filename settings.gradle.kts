import groovy.json.JsonOutput.toJson
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly
import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.sun.plugins.isCI

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
				includeGroup("com.gradle.enterprise")
				includeGroup("gradle.plugin.org.gradle.android")
				includeGroup("org.jetbrains.kotlin.android")
				includeGroup("io.gitlab.arturbosch.detekt")
			}
		}
	}
}

plugins {
	id("com.gradle.enterprise") version "3.14"
	id("net.twisterrob.gradle.plugin.nagging") version "0.16"
	id("project-settings")
}

gradleEnterprise {
	buildScan {
		termsOfServiceUrl = "https://gradle.com/terms-of-service"
		termsOfServiceAgree = "yes"
		if (isCI) {
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
	val error: (String) -> Unit = (if (isCI) ::error else logger::warn)
	error("Android Studio version changed, please remove hack.")
}

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"For more information, please refer to " +
		"https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath" +
		" in the Gradle documentation.",
	"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
)

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"For more information, please refer to " +
		"https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath" +
		" in the Gradle documentation.",
	"at org.jetbrains.plugins.gradle.tooling.util.SourceSetCachedFinder.createArtifactsMap"
)

// TODEL Gradle 8.2 sync in AS FL 2022.2.1 / IDEA 2023.1 https://youtrack.jetbrains.com/issue/IDEA-320266.
// AGP fixed 7.4.0-beta02 and 8.0.0-alpha02 https://issuetracker.google.com/issues/241354494
@Suppress("MaxLineLength")
if ((System.getProperty("idea.version") ?: "") < "2023.1") {
	doNotNagAbout(
		"The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#java_convention_deprecation",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)
	doNotNagAbout(
		"The Project.getConvention() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.Convention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)

	doNotNagAbout(
		"The Project.getConvention() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.builder.ProjectExtensionsDataBuilderImpl.buildAll(ProjectExtensionsDataBuilderImpl.groovy:40)"
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.Convention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.builder.ProjectExtensionsDataBuilderImpl.buildAll(ProjectExtensionsDataBuilderImpl.groovy:41)"
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#java_convention_deprecation",
		// at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:108)
		// at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:117)
		// at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:118)
		"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:1"
	)
	// No method and line number in stack to match all these:
	//  * getJavaPluginConvention(JavaPluginUtil.java:13)
	//  * getSourceSetContainer(JavaPluginUtil.java:18)
	//  * getSourceSetContainer(JavaPluginUtil.java:19)
	doNotNagAbout(
		"The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#java_convention_deprecation",
		"at org.jetbrains.plugins.gradle.tooling.util.JavaPluginUtil."
	)
	doNotNagAbout(
		"The Project.getConvention() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.util.JavaPluginUtil."
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.Convention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.util.JavaPluginUtil."
	)
} else {
	val error: (String) -> Unit = (if (isCI) ::error else logger::warn)
	error("Android Studio version changed, please review hack.")
}

// TODEL Gradle 8.2 sync in AS FL https://youtrack.jetbrains.com/issue/IDEA-320307, maybe fixed in AS HH, probably I.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The BuildIdentifier.getName() method has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Use getBuildPath() to get a unique identifier for the build. " +
		"Consult the upgrading guide for further information: " +
		"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
	// There are 4 stack traces coming to this line, ignore them all at once.
	"at org.jetbrains.plugins.gradle.tooling.util.resolve.DependencyResolverImpl.resolveDependencies(DependencyResolverImpl.java:266)"
)

// TODEL Gradle 8.2 vs AGP https://issuetracker.google.com/issues/279306626
// Gradle 8.2 M1 added nagging for BuildIdentifier.*, which was not replaced in AGP 8.0.x yet.
@Suppress("MaxLineLength")
if (com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION < "8.1.0") {
	doNotNagAbout( // :lintReportDebug, :lintAnalyzeDebug
		"The BuildIdentifier.isCurrentBuild() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.ide.dependencies.ResolvedArtifact.computeModelAddress(ResolvedArtifact.kt:162)"
		// "at com.android.build.gradle.internal.lint.LintModelWriterTask.doTaskAction(LintModelWriterTask.kt:80)"
	)
	doNotNagAbout( // :lintAnalyzeDebug
		"The BuildIdentifier.isCurrentBuild() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.ide.dependencies.ArtifactHandler.handleArtifact(ArtifactHandler.kt:85)"
		// "at com.android.build.gradle.internal.lint.LintModelWriterTask.doTaskAction(LintModelWriterTask.kt:80)"
	)
	doNotNagAbout( // :lintAnalyzeDebug
		"The BuildIdentifier.isCurrentBuild() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.lint.ProjectKeyKt.asProjectSourceSetKey(ProjectKey.kt:83)"
	)
	doNotNagAbout( // :lintAnalyzeDebug
		"The BuildIdentifier.isCurrentBuild() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.lint.ProjectKeyKt.asProjectKey(ProjectKey.kt:45)"
	)
	doNotNagAbout( // :lintAnalyzeDebug
		"The BuildIdentifier.isCurrentBuild() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.lint.AndroidLintTask.writeLintModelFile(AndroidLintTask.kt:300)"
	)
	doNotNagAbout( // Android Studio Flamingo Sync https://issuetracker.google.com/issues/279306626#comment4
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.ide.dependencies.LibraryServiceImpl\$getProjectInfo\$1.apply(LibraryService.kt:138)"
	)

	// All the below ones are not mentioned in the issue.

	doNotNagAbout( // :app:checkReleaseAarMetadata
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.tasks.CheckAarMetadataTask.doTaskAction(CheckAarMetadataTask.kt:118)"
	)
	doNotNagAbout( // :app:mergeDebugResources
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.tasks.MergeResources.doTaskAction(MergeResources.kt:320)"
	)
	doNotNagAbout( // :app:processDebugMainManifest
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.tasks.ProcessApplicationManifest.doTaskAction(ProcessApplicationManifest.kt:164)"
	)
	doNotNagAbout( // :app:processDebugAndroidTestManifest
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.tasks.ProcessTestManifest.doTaskAction(ProcessTestManifest.kt:127)"
	)
	doNotNagAbout( // :app:mergeDebugAssets
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.tasks.MergeSourceSetFolders.doTaskAction(MergeSourceSetFolders.kt:123)"
	)
} else {
	val error: (String) -> Unit = (if (isCI) ::error else logger::warn)
	error("AGP version changed, please review hack.")
}
