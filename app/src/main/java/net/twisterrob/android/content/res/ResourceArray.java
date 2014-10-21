package net.twisterrob.android.content.res;

public abstract class ResourceArray<T> {
	public static final int NOT_FOUND = -1;

	public T getValue(int position) {
		return at(position);
	}

	public int getPosition(Object value) {
		for (int i = 0; i < size(); ++i) {
			if (at(i).equals(value)) {
				return i;
			}
		}
		return NOT_FOUND;
	}

	public abstract int size();

	protected abstract T at(int position);

	public int first() {
		return 0;
	}
	public int last() {
		return size() - 1;
	}
}
