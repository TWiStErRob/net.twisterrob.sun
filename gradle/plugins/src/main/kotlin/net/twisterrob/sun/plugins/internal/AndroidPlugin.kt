package net.twisterrob.sun.plugins.internal

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

internal class AndroidPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.android {
			namespace = "net.twisterrob.sun.${project.name}"
			compileSdk = project.libs.versions.compileSdkVersion.get().toInt()
			defaultConfig.apply {
				minSdk = project.libs.versions.minSdkVersion.get().toInt()
				testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
			}
			lint.apply {
				baseline = project.rootProject
					.file("config/lint/baseline/lint_baseline-${project.slug}.xml")
				lintConfig = project.rootProject
					.file("config/lint/lint.xml")
			}
			testOptions.apply {
				unitTests {
					all { task: Test ->
						// Hide (https://stackoverflow.com/a/79098701/253468)
						// > Java HotSpot(TM) 64-Bit Server VM warning:
						// > Sharing is only supported for boot loader classes because bootstrap classpath has been appended
						task.jvmArgs("-Xshare:off")

						// https://robolectric.org/getting-started/#running-with-java-17-and-higher
						// > java.lang.RuntimeException: Failed to interact with raw FileDescriptor internals; perhaps JRE has changed?
						// > at org.robolectric.interceptors.AndroidInterceptors$FileDescriptorInterceptor.setInt(AndroidInterceptors.java:88)
						// > at com.android.internal.os.ApplicationSharedMemory.create(ApplicationSharedMemory.java:94)
						// > at org.robolectric.android.internal.AndroidTestEnvironment.setUpApplicationState(AndroidTestEnvironment.java:245)
						// > at org.robolectric.RobolectricTestRunner.beforeTest(RobolectricTestRunner.java:327)
    					// > Caused by: java.lang.IllegalAccessException: class org.robolectric.interceptors.AndroidInterceptors$FileDescriptorInterceptor
						// > cannot access class jdk.internal.access.SharedSecrets (in module java.base)
						// > because module java.base does not export jdk.internal.access to unnamed module @21dfe62d
						// > at org.robolectric.interceptors.AndroidInterceptors$FileDescriptorInterceptor.setInt(AndroidInterceptors.java:83)
						// > at com.android.internal.os.ApplicationSharedMemory.$$robo$$com_android_internal_os_ApplicationSharedMemory$create(ApplicationSharedMemory.java:106)
						// > at com.android.internal.os.ApplicationSharedMemory.create(ApplicationSharedMemory.java)
						// > ... 14 more
						task.jvmArgs("--add-opens=java.base/jdk.internal.access=ALL-UNNAMED")

						if (task.javaVersion.isCompatibleWith(JavaVersion.VERSION_21)) {
							// https://github.com/mockito/mockito/issues/3037#issuecomment-1588199599
							task.jvmArgs("-XX:+EnableDynamicAgentLoading")
						}
					}
				}
			}
		}
	}
}

private val Project.slug: String
	get() = path.substringAfter(':').replace(":", "+")
