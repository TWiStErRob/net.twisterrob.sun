package net.twisterrob.sun.android.logic;

import org.junit.Rule;
import org.junit.Test;

import android.view.View;

import app.cash.paparazzi.Paparazzi;

import net.twisterrob.sun.android.R;

public class VerifyTest {

	@Rule
	public Paparazzi paparazzi = new Paparazzi();

	@Test
	public void test() {
		snapshot(paparazzi.inflate(R.layout.widget_1x1));
	}

	private void snapshot(View view) {
		paparazzi.snapshot(view, null, null, null, null);
	}
}
