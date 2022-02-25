object Deps {

	object Android {

		/**
		 * When changing this, update CI too (platforms;android-xx).
		 */
		const val compileSdkVersion = 31

		const val minSdkVersion = 14

		/**
		 * TODEL 211012777 when updating from 7.0
		 */
		const val plugin = "com.android.tools.build:gradle:7.0.4"
		const val cacheFix = "gradle.plugin.org.gradle.android:android-cache-fix-gradle-plugin:2.4.5"
	}

	object Kotlin {

		const val version = "1.5.31"
		val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${version}"

		val detekt = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0"
	}
}
