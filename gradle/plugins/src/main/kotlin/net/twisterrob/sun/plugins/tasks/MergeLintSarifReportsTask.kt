package net.twisterrob.sun.plugins.tasks

import com.android.utils.SdkUtils
import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.ReportingDescriptor
import io.github.detekt.sarif4k.Result
import io.github.detekt.sarif4k.Run
import io.github.detekt.sarif4k.SarifSchema210
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

		val tools = sarifs.mapValues { it.value.run.tool.driver.name }
			.entries.groupBy({ it.value }, { it.key })
		require(tools.size == 1) {
			"Cannot merge sarifs from different tools:\n$tools"
		}

		val projectRoot = SdkUtils.fileToUrlString(project.rootDir)

		@Suppress("NestedLambdaShadowedImplicitParameter")
		val mergedSarif = sarifs.values.first().let {
			it.copy(
				runs = listOf(
					it.run.let {
						it.copy(
							// Don't map original, as it might be empty if there are no results in that sarif file.
							originalURIBaseIDS = mapOf(
								"%SRCROOT%" to ArtifactLocation(
									uri = projectRoot
								)
							),
							results = sarifs.values.flatMap { sarif ->
								sarif.results.map {
									it.relocate(sarif, projectRoot)
								}
							},
							tool = it.tool.let {
								it.copy(
									driver = it.driver.copy(
										rules = mergeRules(sarifs.values)
									)
								)
							}
						)
					}
				)
			)
		}
		output.writeText(SarifSerializer.toJson(mergedSarif))
	}

	private fun mergeRules(sarifs: Collection<SarifSchema210>): List<ReportingDescriptor> =
		sarifs
			// Flatten all the rules into one list.
			.flatMap { it.run.tool.driver.rules.orEmpty() }
			// Deduplicate rules by their IDs,
			.groupBy { it.id }
			// take first instance of each based on the assumption that their IDs are unique.
			.map { it.value.first() }


	@Suppress("NestedLambdaShadowedImplicitParameter")
	private fun Result.relocate(sarif: SarifSchema210, common: String): Result {
		// originalURIBaseIDS.uri = file:///P:/projects/workspace/net.twisterrob.sun/feature/configuration/
		// common = file:///P:/projects/workspace/net.twisterrob.sun/
		// modulePath = feature/configuration/
		val baseUris = sarif.run.originalURIBaseIDS!!
		require(baseUris.size == 1) {
			"Sarif file has multiple base uris: ${baseUris}."
		}
		val modulePath = baseUris.values.single().uri!!.removePrefix(common)
		return this.copy(
			locations = this.locations?.map {
				it.copy(
					physicalLocation = it.physicalLocation?.let {
						it.copy(
							artifactLocation = it.artifactLocation?.let {
								it.copy(
									uri = it.uri?.let { uri ->
										// uri = src/main/java/net/twisterrob/android/app/WidgetConfigurationActivity.java
										modulePath + uri
									}
								)
							}
						)
					}
				)
			}
		)
	}
}

private val SarifSchema210.run: Run
	get() = this.runs.single()

private val SarifSchema210.results: Collection<Result>
	get() = this.run.results.orEmpty()
