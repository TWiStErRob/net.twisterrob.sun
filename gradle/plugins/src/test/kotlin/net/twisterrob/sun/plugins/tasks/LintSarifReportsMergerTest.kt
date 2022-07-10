package net.twisterrob.sun.plugins.tasks

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher
import org.skyscreamer.jsonassert.comparator.CustomComparator
import java.io.File
import java.io.InputStream

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
		val input1 = temp.newFileWithFolder("component/states/build/reports/lint-results-debug.sarif")
			.also { it.fromTestResource("java-and-res/input1-states.sarif") }
			.also { it.replace("file:///P:/projects/workspace/net.twisterrob.sun/", project)}
		val input2 = temp.newFileWithFolder("component/widget/build/reports/lint-results-debug.sarif")
			.also { it.fromTestResource("java-and-res/input2-widget.sarif") }
			.also { it.replace("file:///P:/projects/workspace/net.twisterrob.sun/", project)}
		val output = temp.newFileWithFolder("build/reports/lint/merge-debug.sarif")

		val task = project.tasks.create<MergeLintSarifReportsTask>("merge") {
			sarifFiles.from(input1, input2)
			mergedSarifFile.set(output)
		}

		task.merge()

		JSONAssert.assertEquals(
			"Comparing ${output} to ${"java-and-res/merged.sarif"}",
			fromTestResource("java-and-res/merged.sarif")
				.replace("file:///P:/projects/workspace/net.twisterrob.sun/", project),
			output.readText(),
			JSONCompareMode.STRICT,
		)
	}

	@Test fun `merge two files with multiple base URIs`() {
		val project = ProjectBuilder.builder().withProjectDir(temp.newFolder("module")).build()
		val input1 = temp.newFileWithFolder("feature/preview/build/reports/lint-results-debug.sarif")
			.also { it.fromTestResource("merge-multi-src/input1-preview.sarif") }
			.also { it.replace("file:///home/runner/work/net.twisterrob.sun/net.twisterrob.sun/", project)}
		val input2 = temp.newFileWithFolder("feature/configuration/build/reports/lint-results-debug.sarif")
			.also { it.fromTestResource("merge-multi-src/input2-configuration.sarif") }
			.also { it.replace("file:///home/runner/work/net.twisterrob.sun/net.twisterrob.sun/", project)}
		val output = temp.newFileWithFolder("build/reports/lint/merge-debug.sarif")
//			.also { it.replace("file:///home/runner/work/net.twisterrob.sun/net.twisterrob.sun/", project)}

		val task = project.tasks.create<MergeLintSarifReportsTask>("merge") {
			sarifFiles.from(input1, input2)
			mergedSarifFile.set(output)
		}

		task.merge()

		JSONAssert.assertEquals(
			"Comparing ${output} to ${"merge-multi-src/merged.sarif"}",
			fromTestResource("merge-multi-src/merged.sarif")
				.replace("file:///home/runner/work/net.twisterrob.sun/net.twisterrob.sun/", project),
			output.readText(),
			JSONCompareMode.STRICT,
		)
	}
}

private fun File.fromTestResource(relativePath: String) {
	testResource(relativePath).use { from ->
		this.outputStream().use { to ->
			from.copyTo(to)
		}
	}
}

private fun fromTestResource(relativePath: String): String =
	testResource(relativePath).use { it.reader().readText() }

private fun testResource(relativePath: String): InputStream =
	LintSarifReportsMergerTest::class.java
		.getResourceAsStream("LintSarifReportsMergerTest/$relativePath")!!

private fun TemporaryFolder.newFileWithFolder(path: String): File {
	val folders = path.split("/", "\\").dropLast(1)
	if (folders.isNotEmpty()) {
		newFolder(*folders.toTypedArray())
	}
	return newFile(path)
}

private fun File.replace(hardcodedPath: String, project: Project) {
	this.writeText(this.readText().replace(hardcodedPath, project))
}

private fun String.replace(hardcodedPath: String, project: Project): String {
	val root = project.rootDir.toURI().toString().replaceFirst("file:/", "file:///")
	return this.replace(hardcodedPath, root)
}
