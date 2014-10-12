package net.twisterrob.android.content.res;

import android.content.res.Resources;

public class ResourceArray {
	private final Type arrayType;
	private final int arrayResourceID;
	private Object[] values;

	public ResourceArray(Type arrayType, int arrayResourceID) {
		this.arrayType = arrayType;
		this.arrayResourceID = arrayResourceID;
	}

	public void initialize(Resources res) {
		switch (arrayType) {
			case Int:
				int[] intArr = res.getIntArray(arrayResourceID);
				Integer[] arr = new Integer[intArr.length];
				for (int i = 0; i < arr.length; ++i) {
					arr[i] = intArr[i];
				}
				values = arr;
				break;
			case String:
				values = res.getStringArray(arrayResourceID);
				break;
			case Text:
				values = res.getTextArray(arrayResourceID);
				break;
			default:
				throw new UnsupportedOperationException(arrayType + " is not supported");
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(int position) {
		checkValues();
		return (T)values[position];
	}

	public int getPosition(Object value) {
		checkValues();
		for (int i = 0; i < values.length; ++i) {
			if (values[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public int size() {
		checkValues();
		return values.length;
	}
	public int first() {
		return 0;
	}
	public int last() {
		return size() - 1;
	}

	private void checkValues() {
		if (values == null) {
			throw new IllegalStateException("Array not initialized with a Resources object!");
		}
	}
	public static enum Type {
		Text,
		Int,
		String
	}
}
