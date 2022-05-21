package net.twisterrob.sun.plugins

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import net.twisterrob.gradle.internal.android.unwrapCast
import org.gradle.api.Project
import org.gradle.api.plugins.PluginInstantiationException
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named

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
			val lintReportMergeSarif = rootProject.tasks.named<ReportMergeTask>("lintReportMergeSarif")
			androidComponents.onVariants { variant ->
				val sarif = variant.artifacts.unwrapCast<com.android.build.api.artifact.impl.ArtifactsImpl>()
					.get(com.android.build.gradle.internal.scope.InternalArtifactType.LINT_SARIF_REPORT)
//				finalizedBy(lintReportMergeSarif)
				lintReportMergeSarif.configure { input.from(sarif) }
			}
		}
	}
}

val Project.androidComponents: AndroidComponentsExtension<*, *, *>
		get() {
			// REPORT hasPlugin("com.android.base") should be equivalent, but returns false during plugins.withType<ABP> { }
			// because com.android.build.gradle.internal.plugins.BasePlugin applies ABP class not ID?
			if (!this.plugins.hasPlugin(AndroidBasePlugin::class.java)) {
				throw PluginInstantiationException("Cannot use androidComponents before the Android plugins are applied.")
			}
			return this.extensions["androidComponents"] as AndroidComponentsExtension<*, *, *>
		}
