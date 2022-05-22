package net.twisterrob.sun.plugins.internal

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import net.twisterrob.gradle.android.androidComponents
import net.twisterrob.gradle.internal.android.unwrapCast
import net.twisterrob.sun.plugins.tasks.MergeLintSarifReportsTask
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

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

			sarifReport = true
			androidComponents.onVariants { variant ->
				val mergeTask =
					rootProject.tasks.maybeRegister<MergeLintSarifReportsTask>("lintReportMergeSarif${variant.name.capitalized()}") {
						mergedSarifFile.set(project.layout.buildDirectory.file("reports/lint/merge-${variant.name}.sarif"))
					}

				val sarifProvider = variant.artifacts
					.unwrapCast<com.android.build.api.artifact.impl.ArtifactsImpl>()
					.get(com.android.build.gradle.internal.scope.InternalArtifactType.LINT_SARIF_REPORT)
				mergeTask.configure { sarifFiles.from(sarifProvider) }

				// Always re-run merging when lint was executed.
				tasks.withType<AndroidLintTask>().all {
					afterEvaluate {
						// In onVariants the AndroidVariantTask.variantName is not assigned yet.
						if (this@all.variantName == variant.name) finalizedBy(mergeTask)
					}
				}
			}
		}
	}
}
