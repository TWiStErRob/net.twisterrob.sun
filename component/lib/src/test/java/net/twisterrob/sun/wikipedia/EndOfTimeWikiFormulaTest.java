package net.twisterrob.sun.wikipedia;

import org.junit.Before;

import net.twisterrob.sun.test.SeasonFormulaTest;

public class EndOfTimeWikiFormulaTest extends SeasonFormulaTest {
	@Before
	public void setUpDeclination() {
		formula = new EndOfTimeWikiFormula();
	}
}
