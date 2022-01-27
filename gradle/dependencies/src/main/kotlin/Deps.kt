object Deps {

	object Android {

		/**
		 * When changing this, update CI too (platforms;android-xx).
		 */
		const val compileSdkVersion = 31

		const val minSdkVersion = 14

		const val targetSdkVersion = 31

		/**
		 * TODEL 211012777 when updating from 7.0
		 */
		const val plugin = "com.android.tools.build:gradle:7.0.4"
		const val cacheFix = "gradle.plugin.org.gradle.android:android-cache-fix-gradle-plugin:2.4.5"
	}

	object AndroidX {

		const val appcompat = "androidx.appcompat:appcompat:1.4.1"
		const val activity = "androidx.activity:activity:1.4.0"
		const val fragment = "androidx.fragment:fragment:1.4.0"
		const val annotations = "androidx.annotation:annotation:1.3.0"
		const val constraint = "androidx.constraintlayout:constraintlayout:2.1.3"
	}

	object ThirdParty {
		const val permissions ="pub.devrel:easypermissions:3.0.0"
	}

	object Kotlin {

		const val version = "1.5.31"
		val bom = "org.jetbrains.kotlin:kotlin-bom:${version}"
		val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${version}"
		val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${version}"
		val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:${version}"
	}

	object Test {

		const val paparazzi = "app.cash.paparazzi:paparazzi:0.9.0"
		const val junit4 = "junit:junit:4.13.2"
		const val mockito = "org.mockito:mockito-inline:4.2.0"
		const val paramInjector = "com.google.testparameterinjector:test-parameter-injector:1.5"
	}
}
