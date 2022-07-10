package net.twisterrob.sun.plugins.tasks

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.io.File

/**
 * @see MergeLintSarifReportsTask.merge
 */
class LintSarifReportsMergerTest {

	@get:Rule
	val temp: TemporaryFolder = TemporaryFolder.builder()
		.assureDeletion()
		.build()

	@Test fun `merge two files from different modules`() {
		val project = ProjectBuilder.builder().withProjectDir(temp.newFolder("module")).build()
		val testRes = project.createTestResourceLoader(
			"file:///P:/projects/workspace/net.twisterrob.sun/"
		)
		val input1 = temp.newFiles("component/states/build/reports/lint-results-debug.sarif")
			.also { it.writeText(testRes.load("java-and-res/input1-states.sarif")) }
		val input2 = temp.newFiles("component/widget/build/reports/lint-results-debug.sarif")
			.also { it.writeText(testRes.load("java-and-res/input2-widget.sarif")) }
		val output = temp.newFiles("build/reports/lint/merge-debug.sarif")

		val task = project.tasks.create<MergeLintSarifReportsTask>("merge") {
			sarifFiles.from(input1, input2)
			mergedSarifFile.set(output)
		}

		task.merge()

		JSONAssert.assertEquals(
			"Comparing ${output} to ${"java-and-res/merged.sarif"}",
			testRes.load("java-and-res/merged.sarif"),
			output.readText(),
			JSONCompareMode.STRICT,
		)
	}

	@Test fun `merge two files with multiple base URIs`() {
		val project = ProjectBuilder.builder().withProjectDir(temp.newFolder("module")).build()
		val testRes = project.createTestResourceLoader(
			"file:///home/runner/work/net.twisterrob.sun/net.twisterrob.sun/"
		)
		val input1 = temp.newFiles("feature/preview/build/reports/lint-results-debug.sarif")
			.also { it.writeText(testRes.load("merge-multi-src/input1-preview.sarif")) }
		val input2 = temp.newFiles("feature/configuration/build/reports/lint-results-debug.sarif")
			.also { it.writeText(testRes.load("merge-multi-src/input2-configuration.sarif")) }
		val output = temp.newFiles("build/reports/lint/merge-debug.sarif")

		val task = project.tasks.create<MergeLintSarifReportsTask>("merge") {
			sarifFiles.from(input1, input2)
			mergedSarifFile.set(output)
		}

		task.merge()

		JSONAssert.assertEquals(
			"Comparing ${output} to ${"merge-multi-src/merged.sarif"}",
			testRes.load("merge-multi-src/merged.sarif"),
			output.readText(),
			JSONCompareMode.STRICT,
		)
	}
}

private fun Project.createTestResourceLoader(hardcodedPath: String): ResourceLoader =
	ResourceLoader(
		hardcodedPath,
		rootDir.toURI().toString().replaceFirst("file:/", "file:///")
	)

private class ResourceLoader(
	private val hardcodedPath: String,
	private val testProjectRoot: String
) {

	fun load(path: String): String =
		LintSarifReportsMergerTest::class.java
			.getResourceAsStream("LintSarifReportsMergerTest/$path")!!
			.use { it.reader().readText() }
			.replace(hardcodedPath, testProjectRoot)
}

private fun TemporaryFolder.newFiles(path: String): File {
	val folders = path.split("/", "\\").dropLast(1)
	if (folders.isNotEmpty()) {
		newFolder(*folders.toTypedArray())
	}
	return newFile(path)
}
