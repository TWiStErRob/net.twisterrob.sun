package net.twisterrob.sun.plugins

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.commonAndroidConfig() {
	extensions.configure<BaseExtension> {
		// When changing this, update CI too (platforms;android-xx).
		compileSdkVersion(23)
		defaultConfig.apply {
			minSdkVersion(10)
		}
	}
}
