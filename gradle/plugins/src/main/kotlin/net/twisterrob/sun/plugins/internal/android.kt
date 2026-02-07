package net.twisterrob.sun.plugins.internal

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType

internal fun Project.android(block: CommonExtension.() -> Unit) {
	this.extensions.configure(block)
}

internal fun Project.androidOptional(block: CommonExtension.() -> Unit) {
	if (this.extensions.findByType<CommonExtension>() != null) {
		this.android(block)
	} else {
		// Do nothing, this is not an Android module.
	}
}
