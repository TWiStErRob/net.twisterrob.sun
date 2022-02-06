import net.twisterrob.gradle.android.version

plugins {
	id("project-module-android-app")
	id("project-dependencies")
}

dependencies {
	implementation(project(":feature:configuration"))
	implementation(project(":component:widget"))
	implementation(project(":component:theme"))
	debugImplementation(project(":feature:preview"))
}

android {
	defaultConfig {
		applicationId = "net.twisterrob.sun"
		targetSdk = Deps.Android.targetSdkVersion
		(this as com.android.build.gradle.internal.dsl.DefaultConfig).version {
			versionNameFormat = "%1\$d.%2\$d.%3\$d#${VCS.current.revision}"
		}
	}
	lint {
		// TODEL AGP 7.2 https://issuetracker.google.com/issues/194525628
		if (System.getProperty("idea.is.internal").toBoolean()) {
			isCheckReleaseBuilds = false
		}
	}
}
