package com.cs4321.app;

/**
 * A tuple is used to represent a row from a table in our database.
 * 
 * @author Yohanes
 *
 */
public class Tuple {

	private int[] row;
	private String data;

	/**
	 * Converts a row from a table into a tuple.
	 * 
	 * @param data- A line from a table which we want to represent as a tuple.
	 *              Requires: data contains integers separated by commas.
	 */
	public Tuple(String data) {
		this.data = data;
		if (data.length() == 0) {
			row = new int[0];
			return;
		}
		String[] values = data.split(",");
		row = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			row[i] = Integer.parseInt(values[i]);
		}
	}

	/**
	 * Returns the size of the tuple.
	 * 
	 * @return- The size of the tuple.
	 */
	public int size() {
		return row.length;
	}

	/**
	 * Returns the value at the given index in the tuple.
	 * 
	 * @param index- The position in the tuple we want to return. Requires: 0 <=
	 *               index < this.size().
	 * @return- The value at the given index in the tuple.
	 */
	public int get(int index) {
		return row[index];
	}

	/**
	 * Splices this Tuple from startIndex to endIndex but not including the element
	 * at endIndex
	 * 
	 * @param startIndex The position to start splicing from. Requires: 0 <=
	 *                   startIndex <= this.size()
	 * @param endIndex   The smallest position to not include in the splice.
	 *                   Requires startIndex <= endIndex <= this.size()
	 * @return
	 */
	public Tuple get(int startIndex, int endIndex) {
		StringBuilder sb = new StringBuilder();
		for (int i = startIndex; i < endIndex; i++) {
			sb.append(row[i] + "");
			if (i < endIndex - 1) {
				sb.append(",");
			}
		}
		return new Tuple(sb.toString());
	}

	/**
	 * Returns a string representation of a tuple.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.size(); i++) {
			sb.append(row[i] + "");
			if (i < this.size() - 1)
				sb.append(",");
		}
		return sb.toString();
	}

	/**
	 * Checks if a tuple is equivalent to another object. Only returns true if the
	 * object is a tuple and the contents at each index are the same.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Tuple))
			return false;
		Tuple t = (Tuple) o;
		if (this.size() != t.size())
			return false;
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i) != this.get(i))
				return false;
		}
		return true;
	}

	/**
	 * Creates a new tuple of size [this.size() + t.size()]. The tuple has the
	 * contents of t appended
	 * to the contents of this.
	 * 
	 * @param t - the tuple to combine this tuple with
	 * @return - a new tuple with the contents of t added to the end of this
	 */
	public Tuple concat(Tuple t) {
		StringBuilder newData = new StringBuilder(this.data);
		if (this.data.length() > 0)
			newData.append(',');
		newData.append(t.data);
		return new Tuple(newData.toString());
	}
}
