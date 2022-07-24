package net.twisterrob.sun.plugins.internal

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.android(block: CommonExtension<*, *, *, *>.() -> Unit) {
	project.extensions.configure(block)
}
