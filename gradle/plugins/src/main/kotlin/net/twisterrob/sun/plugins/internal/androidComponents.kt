package net.twisterrob.sun.plugins.internal

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginInstantiationException
import org.gradle.kotlin.dsl.get

internal val Project.androidComponents: AndroidComponentsExtension<*, *, *>
		get() {
			if (!this.plugins.hasPlugin(AndroidBasePlugin::class.java)) {
				throw PluginInstantiationException("Cannot use androidComponents before the Android plugins are applied.")
			}
			return this.extensions["androidComponents"] as AndroidComponentsExtension<*, *, *>
		}
