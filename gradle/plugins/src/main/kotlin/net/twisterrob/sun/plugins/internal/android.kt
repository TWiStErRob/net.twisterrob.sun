package net.twisterrob.sun.plugins.internal

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.android(block: CommonExtension<*, *, *, *>.() -> Unit) {
	project.extensions.configure<BaseExtension> {
		block(this as CommonExtension<*, *, *, *>)
	}
}
