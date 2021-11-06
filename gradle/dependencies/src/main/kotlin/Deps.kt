object Deps {

	object Android {
		const val compileSdkVersion = 29

		/**
		 * When changing this, update CI too (platforms;android-xx).
		 */
		const val minSdkVersion = 11

		/**
		 * TODEL ExpiredTargetSdkVersion when updating
		 * TOFIX MissingPermission when updating
		 */
		const val targetSdkVersion = 19

		const val plugin = "com.android.tools.build:gradle:4.2.0"
		const val cacheFix = "gradle.plugin.org.gradle.android:android-cache-fix-gradle-plugin:2.4.4"
	}

	object AndroidX {

		const val v4 = "com.android.support:support-v4:23.2.0"
		const val annotations = "com.android.support:support-annotations:23.2.0"
	}

	object Kotlin {

		const val version = "1.5.31"
		val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${version}"
		val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${version}"
		val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:${version}"
	}

	object Test {
		const val junit4 = "junit:junit:4.13.2"
		const val mockito = "org.mockito:mockito-core:4.0.0"
		const val paramInjector = "com.google.testparameterinjector:test-parameter-injector:1.5"
	}
}
