package net.twisterrob.sun.plugins

import org.junit.Assert.assertEquals
import org.junit.Test

class MergeLintSarifReportsTaskTest_commonPath {

	@Test
	fun `only file name differs`() {
		assertEquals(
			"/C:/foo/bar/baz/",
			commonPath(
				listOf(
					"/C:/foo/bar/baz/yee.xxx",
					"/C:/foo/bar/baz/yoo.xxx",
				)
			)
		)
	}

	@Test
	fun `different last segment`() {
		assertEquals(
			"/C:/foo/bar/",
			commonPath(
				listOf(
					"/C:/foo/bar/baz/yee.xxx",
					"/C:/foo/bar/bazz/yee.xxx",
				)
			)
		)
	}

	@Test
	fun `different subdirs`() {
		assertEquals(
			"/C:/foo/",
			commonPath(
				listOf(
					"/C:/foo/bar/baz/yee.xxx",
					"/C:/foo/bar/bazz/yee.xxx",
					"/C:/foo/baz/yoo.xxx",
				)
			)
		)
	}

	@Test
	fun `different directories`() {
		assertEquals(
			"/C:/",
			commonPath(
				listOf(
					"/C:/foo/bar/baz/yee.xxx",
					"/C:/foo/bar/bazz/yee.xxx",
					"/C:/fooz/baz/yoo.xxx",
				)
			)
		)
	}

	@Test
	fun `different drives`() {
		assertEquals(
			"/",
			commonPath(
				listOf(
					"/C:/foo/bar/baz/yee.xxx",
					"/D:/foo/bar/baz/yee.xxx",
				)
			)
		)
	}

	@Test
	fun `tricky path`() {
		assertEquals(
			"/foo/",
			commonPath(
				listOf(
					"/foo/bar.baz",
					"/foo/bar/baz",
				)
			)
		)
	}

	@Test
	fun `folder and file`() {
		assertEquals(
			"/foo/",
			commonPath(
				listOf(
					"/foo/bar/",
					"/foo/bar",
				)
			)
		)
	}

	@Test
	fun `root as common`() {
		assertEquals(
			"/",
			commonPath(
				listOf(
					"/foo",
					"/bar",
				)
			)
		)
	}

	@Test
	fun `root as input`() {
		assertEquals(
			"/",
			commonPath(
				listOf(
					"/foo",
					"/",
				)
			)
		)
	}

	@Test
	fun `unix only file name differs`() {
		assertEquals(
			"/foo/bar/",
			commonPath(
				listOf(
					"/foo/bar/baz/yee.xxx",
					"/foo/bar/bazz/yoo.xxx",
				)
			)
		)
	}
}
