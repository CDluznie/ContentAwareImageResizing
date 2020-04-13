package cair.graph;

/**
 * An edge in the flow graph
 **/
public class Edge {

	private final int from;
	private final int to;
	private final int capacity;
	private int used;
	
	/**
	 * Construct an edge of the flow graph
	 * @param from Origin vertex of the edge
	 * @param to Destination vertex of the edge
	 * @param capacity Capacity of the edge
	 * @param used Used flow of the edge
	 * @throws IllegalArgumentException capacity &lt; 0
	 * @throws IllegalArgumentException used &lt; 0
	 * @throws IllegalArgumentException used &gt; capacity
	 **/
	public Edge(int from, int to, int capacity, int used) {
		if (capacity < 0) {
			throw new IllegalArgumentException("capacity = " + capacity + " must be >= 0");
		}
		if (used < 0 || used > capacity) {
			throw new IllegalArgumentException("used = " + used + " must be >= 0 and <= capacity = " + capacity);
		}
		this.from = from;
		this.to = to;
		this.capacity = capacity;
		this.used = used;
	}
	
	/**
	 * Return a string describing the edge
	 * @return a string describing the edge
	 **/
	@Override
	public String toString() {
		return from + "-(" + used + "/" + capacity + ")->" + to;
	}

	/**
	 * Return the origin vertex of the edge
	 * @return the origin vertex of the edge
	 * @see Edge#getTo
	 **/
	public int getFrom() {
		return from;
	}

	/**
	 * Return the destination vertex of the edge
	 * @return the destination vertex of the edge
	 * @see Edge#getFrom
	 **/
	public int getTo() {
		return to;
	}

	/**
	 * Return the capacity of the edge
	 * @return the capacity of the edge
	 * @see Edge#getUsed
	 **/
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Return the used flow of the edge
	 * @return the used flow of the edge
	 * @see Edge#getCapacity
	 * @see Edge#setUsed
	 **/
	public int getUsed() {
		return used;
	}

	/**
	 * Modify the used flow of the edge
	 * @param used The new used flow of the edge
	 * @throws IllegalArgumentException used &lt; 0
	 * @throws IllegalArgumentException used &gt; getCapacity()
	 * @see Edge#getCapacity
	 * @see Edge#getUsed
	 * @see Edge#fill
	 **/
	public void setUsed(int used) {
		if (used < 0 || used > capacity) {
			throw new IllegalArgumentException("used = " + used + " must be >= 0 and <= getCapacity = " + capacity);
		}
		this.used = used;
	}

	/**
	 * Increment the used flow of the edge
	 * @param used The incrementation value to the used flow
	 * @throws IllegalArgumentException used &lt; 0
	 * @throws IllegalStateException used &gt; getCapacity()
	 * @see Edge#getCapacity
	 * @see Edge#getUsed
	 * @see Edge#setUsed
	 **/
	public void fill (int used) {
		if (used < 0) {
			throw new IllegalArgumentException("used = " + used + " must be >= 0");
		}
		this.used += used;
		if (this.used < 0 || this.used > capacity) {
			throw new IllegalStateException("used = " + this.used + " must be >= 0 and <= getCapacity() = " + capacity);
		}
	}

	/**
	 * Check if the edge is saturated or not
	 * 
	 * The edge is not saturated if the used flow of the edge is lower to the capacity.
	 * 
	 * @return <b>true</b> if the edge is not saturated, <b>false</b> otherwise
	 * @see Edge#getCapacity
	 * @see Edge#getUsed
	 **/
	public boolean isFree() {
		return (this.capacity > this.used);
	}
	
	/**
	 * Return the available value of flow that can go throught the edge
	 * @return the available value of flow that can go throught the edge
	 * @see Edge#getCapacity
	 * @see Edge#getUsed
	 * @see Edge#isFree
	 **/
	public int getFreeFlow() {
		return (this.capacity - this.used);
	}

}
