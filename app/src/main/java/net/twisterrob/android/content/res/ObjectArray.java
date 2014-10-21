package net.twisterrob.android.content.res;

public abstract class ObjectArray<T> extends ResourceArray<T> {
	protected final T[] values;

	public ObjectArray(T[] values) {
		this.values = values;
	}

	@Override public int size() {
		return values.length;
	}

	@Override protected T at(int position) {
		return values[position];
	}
}
