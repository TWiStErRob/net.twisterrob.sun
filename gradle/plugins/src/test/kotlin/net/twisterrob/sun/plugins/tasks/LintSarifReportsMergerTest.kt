package net.twisterrob.sun.plugins.tasks

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
		val project = ProjectBuilder.builder().withProjectDir(File("P:/projects/workspace/net.twisterrob.sun/")).build()
		val input1 = temp.newFile("input1.sarif")
			.also { it.fromTestResource("java-and-res/component/states/build/reports/lint-results-debug.sarif") }
		val input2 = temp.newFile("input2.sarif")
			.also { it.fromTestResource("java-and-res/component/widget/build/reports/lint-results-debug.sarif") }
		val output = temp.newFile("output.sarif")

		val task = project.tasks.create<MergeLintSarifReportsTask>("merge") {
			sarifFiles.from(input1, input2)
			mergedSarifFile.set(output)
		}

		task.merge()

		JSONAssert.assertEquals(
			"Comparing ${output} to ${"java-and-res/build/reports/lint/merge-debug.sarif"}",
			fromTestResource("java-and-res/build/reports/lint/merge-debug.sarif"),
			output.readText(),
				JSONCompareMode.STRICT,
//			CustomComparator(
//				JSONCompareMode.STRICT,
//				SRCROOT(temp.root.parentFile, "module/")
//			)
		)
	}

	@Test fun `merge two files with multiple base URIs`() {
		val project = ProjectBuilder.builder().withProjectDir(File("/home/runner/work/net.twisterrob.sun/net.twisterrob.sun/")).build()
		val input1 = temp.newFile("input1.sarif")
			.also { it.fromTestResource("merge-multi-src/feature/preview/build/reports/lint-results-debug.sarif") }
		val input2 = temp.newFile("input2.sarif")
			.also { it.fromTestResource("merge-multi-src/feature/configuration/build/reports/lint-results-debug.sarif") }
		val output = temp.newFile("output.sarif")

		val task = project.tasks.create<MergeLintSarifReportsTask>("merge") {
			sarifFiles.from(input1, input2)
			mergedSarifFile.set(output)
		}

		task.merge()

		JSONAssert.assertEquals(
			"Comparing ${output} to ${"merge-multi-src/build/reports/lint/merge-debug.sarif"}",
			fromTestResource("merge-multi-src/build/reports/lint/merge-debug.sarif"),
			output.readText(),
			JSONCompareMode.STRICT,
//			CustomComparator(
//				JSONCompareMode.STRICT,
//				SRCROOT(temp.root.parentFile, "module/")
//			)
		)
	}
}

@Suppress("TestFunctionName")
private fun SRCROOT(root: File, relativePath: String): Customization {
	val rootPath = root.toURI().toString().replaceFirst("file:/", "file:///")
	return Customization(
		"runs[0].originalUriBaseIds.%SRCROOT%.uri",
		RegularExpressionValueMatcher("""${Regex.fromLiteral(rootPath)}[^/\\]+/${relativePath}""")
	)
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
