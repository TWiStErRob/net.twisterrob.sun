package net.twisterrob.sun.plugins.tasks

import org.junit.Assert.assertEquals
import org.junit.Test

class MergeLintSarifReportsTaskTest_commonParent {

	@Test
	fun `no input`() {
		assertEquals(
			"/",
			commonParent(
				listOf(
					// empty
				)
			)
		)
	}

	@Test
	fun `empty input`() {
		assertEquals(
			"/",
			commonParent(
				listOf(
					""
				)
			)
		)
	}

	@Test
	fun `only one file`() {
		assertEquals(
			"/foo/bar/baz/",
			commonParent(
				listOf(
					"/foo/bar/baz/yee.xxx",
				)
			)
		)
	}

	@Test
	fun `only one folder`() {
		assertEquals(
			"/foo/bar/baz/",
			commonParent(
				listOf(
					"/foo/bar/baz/",
				)
			)
		)
	}

	@Test
	fun `only root`() {
		assertEquals(
			"/",
			commonParent(
				listOf(
					"/",
				)
			)
		)
	}

	@Test
	fun `prefix increasing`() {
		assertEquals(
			"/foo/bar/",
			commonParent(
				listOf(
					"/foo/bar/",
					"/foo/bar/baz/",
					"/foo/bar/baz/boo/",
				)
			)
		)
	}

	@Test
	fun `prefix decreasing`() {
		assertEquals(
			"/foo/bar/",
			commonParent(
				listOf(
					"/foo/bar/baz/boo/",
					"/foo/bar/baz/",
					"/foo/bar/",
				)
			)
		)
	}

	@Test
	fun `only file name differs`() {
		assertEquals(
			"/C:/foo/bar/baz/",
			commonParent(
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
			commonParent(
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
			commonParent(
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
			commonParent(
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
			commonParent(
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
			commonParent(
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
			commonParent(
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
			commonParent(
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
			commonParent(
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
			commonParent(
				listOf(
					"/foo/bar/baz/yee.xxx",
					"/foo/bar/bazz/yoo.xxx",
				)
			)
		)
	}
}
