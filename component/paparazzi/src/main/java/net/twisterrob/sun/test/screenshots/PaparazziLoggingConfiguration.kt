package net.twisterrob.sun.test.screenshots

import java.util.logging.LogManager

class PaparazziLoggingConfiguration {
	init {
		PaparazziLoggingConfiguration::class.java.getResourceAsStream("/logging.properties").use {
			LogManager.getLogManager().readConfiguration(it)
		}
	}
}
