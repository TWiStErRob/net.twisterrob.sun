package net.twisterrob.sun.plugins

import Deps
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal fun Project.commonAndroidConfig() {
	apply(plugin = "org.gradle.android.cache-fix")
	extensions.configure<BaseExtension> {
		compileSdkVersion(Deps.Android.compileSdkVersion)
		defaultConfig.apply {
			minSdkVersion(Deps.Android.minSdkVersion)
		}
	}
}
