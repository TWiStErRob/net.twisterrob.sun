package net.twisterrob.sun.plugins.internal

import org.gradle.accessors.dm.LibrariesForLibs.VersionAccessors
import org.gradle.api.JavaVersion

internal val VersionAccessors.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(java.get())
