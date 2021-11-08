package net.twisterrob.sun.android.logic;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

import net.twisterrob.sun.android.logic.SunAngleFormatter.Result;

@RunWith(Parameterized.class)
public class SunAngleFormatterUnitTest {

	private final float angle;
	private final @NonNull String expectedWhole;
	private final @NonNull String expectedFraction;

	public SunAngleFormatterUnitTest(float angle, @NonNull String expectedWhole, @NonNull String expectedFraction) {
		this.angle = angle;
		this.expectedWhole = expectedWhole;
		this.expectedFraction = expectedFraction;
	}

	@Test
	public void testAngleFormatting() {
		SunAngleFormatter sut = new SunAngleFormatter();

		Result result = sut.format(angle);

		assertEquals(expectedWhole, result.angle);
		assertEquals(expectedFraction, result.fraction);
	}

	@Parameters(name = "{index}: {0} formats as {1}{2}")
	public static Iterable<Object[]> parameters() {
		return Arrays.asList(
				new Object[] {+0.00000001f, "0", ".0000"},
				new Object[] {-0.00000001f, "-0", ".0000"},
				new Object[] {+0.00001f, "0", ".0000"},
				new Object[] {-0.00001f, "-0", ".0000"},
				new Object[] {+0.0001f, "0", ".0001"},
				new Object[] {-0.0001f, "-0", ".0001"},
				new Object[] {+0.001f, "0", ".0010"},
				new Object[] {-0.001f, "-0", ".0010"},
				new Object[] {+0.01f, "0", ".0100"},
				new Object[] {-0.01f, "-0", ".0100"},
				new Object[] {+0.1f, "0", ".1000"},
				new Object[] {-0.1f, "-0", ".1000"},
				new Object[] {+1.0f, "1", ".0000"},
				new Object[] {-1.0f, "-1", ".0000"},
				new Object[] {+12.3456789f, "12", ".3457"},
				new Object[] {-12.3456789f, "-12", ".3457"},
				new Object[] {+0.123456789f, "0", ".1235"},
				new Object[] {-0.123456789f, "-0", ".1235"},
				new Object[] {0f, "0", ".0000"}
		);
	}
}
