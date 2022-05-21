package net.twisterrob.sun.plugins

import io.github.detekt.sarif4k.SarifSerializer
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Based on Detekt's SARIF merging task.
 *
 * @see io.gitlab.arturbosch.detekt.report.ReportMergeTask
 * @see io.gitlab.arturbosch.detekt.report.SarifReportMerger
 */
@CacheableTask
abstract class MergeLintSarifReportsTask : DefaultTask() {

	@get:InputFiles
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val sarifFiles: ConfigurableFileCollection

	@get:OutputFile
	abstract val mergedSarifFile: RegularFileProperty

	@TaskAction
	fun merge() {
		val inputs = sarifFiles.files
		logger.info("Inputs")
		logger.info(inputs.joinToString(separator = "\n") { "${it.absolutePath} (exists=${it.exists()})" })

		val output = mergedSarifFile.get().asFile
		logger.info("Output = ${output.absolutePath}")

		merge(inputs.filter { it.exists() }, output)
		logger.lifecycle("Merged SARIF output to ${output.absolutePath}")
	}

	private fun merge(inputs: Collection<File>, output: File) {
		val sarifs = inputs.associateWith { SarifSerializer.fromJson(it.readText()) }

		val runs = sarifs.mapValues { it.value.runs.size }
		require(runs.values.all { it == 1 }) {
			"Some sarifs have no or multiple runs:\n${runs.filterValues { it != 1 }}"
		}

		val tools = sarifs.mapValues { it.value.runs.single().tool.driver.name }
			.entries.groupBy({ it.value }, { it.key })
		require(tools.size == 1) {
			"Cannot merge sarifs from different tools:\n$tools"
		}

		val mergedResults = sarifs.values.flatMap { it.runs.single().results.orEmpty() }
		// There will be duplicate rules on "id" as key, but GitHub can handle it.
		val mergedRules = sarifs.values.flatMap { it.runs.single().tool.driver.rules.orEmpty() }
		val reference = sarifs.values.first()
		val mergedSarif = reference.copy(
			runs = listOf(
				reference.runs.single().copy(
					results = mergedResults,
					tool = reference.runs.single().tool.copy(
						driver = reference.runs.single().tool.driver.copy(
							rules = mergedRules
						)
					)
				)
			)
		)
		output.writeText(SarifSerializer.toJson(mergedSarif))
	}
}
