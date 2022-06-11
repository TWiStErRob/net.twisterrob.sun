package net.twisterrob.sun.plugins.internal

import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal val Project.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(libs.versions.java.get())
