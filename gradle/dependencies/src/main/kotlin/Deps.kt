object Deps {

	object Android {

		/**
		 * When changing this, update CI too (platforms;android-xx).
		 */
		const val compileSdkVersion = 31

		const val minSdkVersion = 14

		/**
		 * TODEL ExpiredTargetSdkVersion when updating
		 * TOFIX MissingPermission when updating
		 */
		const val targetSdkVersion = 19

		const val plugin = "com.android.tools.build:gradle:4.2.2"
		const val cacheFix = "gradle.plugin.org.gradle.android:android-cache-fix-gradle-plugin:2.4.4"
	}

	object AndroidX {

		const val appcompat = "androidx.appcompat:appcompat:1.4.0-rc01"
		const val annotations = "androidx.annotation:annotation:1.3.0"
	}

	object Kotlin {

		const val version = "1.5.31"
		val bom = "org.jetbrains.kotlin:kotlin-bom:${version}"
		val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${version}"
		val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${version}"
		val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:${version}"
	}

	object Test {

		const val paparazzi = "app.cash.paparazzi:paparazzi:0.8.0"
		const val junit4 = "junit:junit:4.13.2"
		const val mockito = "org.mockito:mockito-core:4.0.0"
		const val paramInjector = "com.google.testparameterinjector:test-parameter-injector:1.5"
	}
}
