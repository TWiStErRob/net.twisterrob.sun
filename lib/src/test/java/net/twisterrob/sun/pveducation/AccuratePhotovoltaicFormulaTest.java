package net.twisterrob.sun.pveducation;

import org.junit.Before;

import net.twisterrob.sun.test.SeasonFormulaTest;

public class AccuratePhotovoltaicFormulaTest extends SeasonFormulaTest {
	@Before
	public void setUpDeclination() {
		formula = new AccuratePhotovoltaicFormula();
	}
}
