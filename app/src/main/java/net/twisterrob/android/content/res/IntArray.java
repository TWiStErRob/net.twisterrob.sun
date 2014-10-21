package net.twisterrob.android.content.res;

import android.content.res.Resources;

public class IntArray extends ResourceArray<Integer> {
	protected final int[] values;

	public IntArray(Resources res, int arrayResourceID) {
		values = res.getIntArray(arrayResourceID);
	}

	@Override public int size() {
		return values.length;
	}

	@Override protected Integer at(int position) {
		return values[position];
	}
}
