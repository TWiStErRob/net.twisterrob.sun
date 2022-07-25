package net.twisterrob.sun.plugins

/**
 * See [GitHub Actions: Default environment variables](https://docs.github.com/en/actions/learn-github-actions/environment-variables#default-environment-variables).
 */
public val isCI: Boolean
	get() = System.getenv("GITHUB_ACTIONS") == "true"
