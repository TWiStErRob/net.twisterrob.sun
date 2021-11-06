package net.twisterrob.sun.plugins

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.commonAndroidConfig() {
	extensions.configure<BaseExtension> {
		compileSdkVersion(Deps.Android.compileSdkVersion)
		defaultConfig.apply {
			minSdkVersion(Deps.Android.minSdkVersion)
		}
	}
}
