package net.twisterrob.sun.plugins.internal

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType

internal fun Project.android(block: CommonExtension<*, *, *, *>.() -> Unit) {
	project.extensions.configure<BaseExtension> {
		block(this as CommonExtension<*, *, *, *>)
	}
}

internal fun Project.androidOptional(block: CommonExtension<*, *, *, *>.() -> Unit) {
	if (project.extensions.findByType<BaseExtension>() != null) {
		this.android(block)
	} else {
		// Do nothing, this is not an Android module.
	}
}
