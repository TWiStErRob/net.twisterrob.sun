package net.twisterrob.sun.plugins.internal

import com.android.build.api.artifact.Artifacts
import net.twisterrob.gradle.android.androidComponents
import net.twisterrob.gradle.internal.android.unwrapCast
import net.twisterrob.sun.plugins.tasks.MergeLintSarifReportsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.configurationcache.extensions.capitalized

class AndroidLintSarifMergePlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.android {
			lint {
				@Suppress("UnstableApiUsage")
				sarifReport = true
			}
		}
		wireLintReportMergeSarif(project)
	}
}

private fun wireLintReportMergeSarif(project: Project) {
	val rootProject = project.rootProject
	val lintReportMergeSarif =
		rootProject.tasks.maybeRegister<Task>("lintReportMergeSarif") {
			// Placeholder task for lifecycle hooking in CI. Will have dependencies from other projects.
		}

	project.androidComponents.onVariants { variant ->
		val lintReportMergeSarifVariant =
			rootProject.tasks.maybeRegister<MergeLintSarifReportsTask>("lintReportMergeSarif${variant.name.capitalized()}") {
				mergedSarifFile.set(project.layout.buildDirectory.file("reports/lint/merge-${variant.name}.sarif"))
			}
		// Will result in multiple dependencies to the same task, but there's no other way.
		// If this was in register's configuration block,
		// it wouldn't be executed when the only task being invoked is :lintReportMergeSarif.
		lintReportMergeSarif.configure { dependsOn(lintReportMergeSarifVariant) }
		lintReportMergeSarifVariant.configure { sarifFiles.from(variant.artifacts.sarifReportFile) }
	}
}

private val Artifacts.sarifReportFile: Provider<RegularFile>
	get() =
		this
			.unwrapCast<com.android.build.api.artifact.impl.ArtifactsImpl>()
			.get(com.android.build.gradle.internal.scope.InternalArtifactType.LINT_SARIF_REPORT)
