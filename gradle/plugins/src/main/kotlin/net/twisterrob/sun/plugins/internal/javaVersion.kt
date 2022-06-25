package net.twisterrob.sun.plugins.internal

import org.gradle.api.JavaVersion
import org.gradle.accessors.dm.LibrariesForLibs.VersionAccessors

internal val VersionAccessors.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(java.get())
