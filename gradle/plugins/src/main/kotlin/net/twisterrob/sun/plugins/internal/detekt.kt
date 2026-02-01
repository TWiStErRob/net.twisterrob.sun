package net.twisterrob.sun.plugins.internal

import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.detekt(block: DetektExtension.() -> Unit) {
	this.extensions.configure(block)
}
