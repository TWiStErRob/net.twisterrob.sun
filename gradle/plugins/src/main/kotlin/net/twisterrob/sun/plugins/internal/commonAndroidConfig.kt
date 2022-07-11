package net.twisterrob.sun.plugins.internal

import com.android.build.api.artifact.Artifacts
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import net.twisterrob.gradle.android.androidComponents
import net.twisterrob.gradle.internal.android.unwrapCast
import net.twisterrob.sun.plugins.tasks.MergeLintSarifReportsTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.configurationcache.extensions.capitalized
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
			val projectSlug = project.path.substringAfter(':').replace(":", "+")
			baseline = rootProject.file("config/lint/baseline/lint_baseline-${projectSlug}.xml")
			lintConfig = rootProject.file("config/lint/lint.xml") 

			sarifReport = true
			val lintReportMergeSarif = rootProject.tasks.named("lintReportMergeSarif")
			androidComponents.onVariants { variant ->
				val lintReportMergeSarifVariant =
					rootProject.tasks.maybeRegister<MergeLintSarifReportsTask>("lintReportMergeSarif${variant.name.capitalized()}") {
						mergedSarifFile.set(project.layout.buildDirectory.file("reports/lint/merge-${variant.name}.sarif"))
					}
				// Will result in multiple dependencies to the same task, but there's no other way.
				// If this was in register's configuration block it wouldn't be executed when
				// The only task being invoked is :lintReportMergeSarif.
				lintReportMergeSarif.configure { dependsOn(lintReportMergeSarifVariant) }
				lintReportMergeSarifVariant.configure { sarifFiles.from(variant.artifacts.sarifReportFile) }
			}
		}
	}
}

private val Artifacts.sarifReportFile: Provider<RegularFile>
	get() =
		this
			.unwrapCast<com.android.build.api.artifact.impl.ArtifactsImpl>()
			.get(com.android.build.gradle.internal.scope.InternalArtifactType.LINT_SARIF_REPORT)
