package net.twisterrob.sun.plugins

import org.junit.Assert.assertEquals
import org.junit.Test

class MergeLintSarifReportsTaskTest_commonPath {

	@Test
	fun `only file name differs`() {
		val result = commonPath(
			listOf(
				"/C:/foo/bar/baz/yee.xxx",
				"/C:/foo/bar/baz/yoo.xxx",
			)
		)
		assertEquals("/C:/foo/bar/baz/", result)
	}

	@Test
	fun `different last segment`() {
		val result = commonPath(
			listOf(
				"/C:/foo/bar/baz/yee.xxx",
				"/C:/foo/bar/bazz/yee.xxx",
			)
		)
		assertEquals("/C:/foo/bar/", result)
	}

	@Test
	fun `different subdirs`() {
		val result = commonPath(
			listOf(
				"/C:/foo/bar/baz/yee.xxx",
				"/C:/foo/bar/bazz/yee.xxx",
				"/C:/foo/baz/yoo.xxx",
			)
		)
		assertEquals("/C:/foo/", result)
	}

	@Test
	fun `different directories`() {
		val result = commonPath(
			listOf(
				"/C:/foo/bar/baz/yee.xxx",
				"/C:/foo/bar/bazz/yee.xxx",
				"/C:/fooz/baz/yoo.xxx",
			)
		)
		assertEquals("/C:/", result)
	}

	@Test
	fun `different drives`() {
		val result = commonPath(
			listOf(
				"/C:/foo/bar/baz/yee.xxx",
				"/D:/foo/bar/baz/yee.xxx",
			)
		)
		assertEquals("/", result)
	}

	@Test
	fun `tricky path`() {
		val result = commonPath(
			listOf(
				"/foo/bar.baz",
				"/foo/bar/baz",
			)
		)
		assertEquals("/foo/", result)
	}

	@Test
	fun `folder and file`() {
		val result = commonPath(
			listOf(
				"/foo/bar/",
				"/foo/bar",
			)
		)
		assertEquals("/foo/", result)
	}

	@Test
	fun `root as common`() {
		val result = commonPath(
			listOf(
				"/foo",
				"/bar",
			)
		)
		assertEquals("/", result)
	}

	@Test
	fun `root as input`() {
		val result = commonPath(
			listOf(
				"/foo",
				"/",
			)
		)
		assertEquals("/", result)
	}

	@Test
	fun `unix only file name differs`() {
		val result = commonPath(
			listOf(
				"/foo/bar/baz/yee.xxx",
				"/foo/bar/bazz/yoo.xxx",
			)
		)
		assertEquals("/foo/bar/", result)
	}
}
