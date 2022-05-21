package net.twisterrob.sun.plugins.internal

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import net.twisterrob.gradle.internal.android.unwrapCast
import net.twisterrob.sun.plugins.tasks.MergeLintSarifReportsTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
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
			val lintReportMergeSarif =
				rootProject.tasks.named<MergeLintSarifReportsTask>("lintReportMergeSarif")
			tasks.withType<AndroidLintTask>().all { finalizedBy(lintReportMergeSarif) }
			androidComponents.onVariants { variant ->
				val sarifProvider = variant.artifacts
					.unwrapCast<com.android.build.api.artifact.impl.ArtifactsImpl>()
					.get(com.android.build.gradle.internal.scope.InternalArtifactType.LINT_SARIF_REPORT)
				lintReportMergeSarif.configure { sarifFiles.from(sarifProvider) }
			}
		}
	}
}
