package draw;

public class Rectangle {

	/**
	 * The points (x,y) and (x2,y2) are the outer-most included points of the
	 * rectangle. They describe two different corner points inside the
	 * rectangle, which two is not specified so make no assumption about the
	 * ordering of the values.
	 */
	public int x, y, x2, y2;

	public Rectangle(int x, int y, int x2, int y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}

	public boolean contains(int x, int y) {
		if (x < this.x && x < this.x2)
			return false;
		if (y < this.y && y < this.y2)
			return false;
		if (x > this.x && x > this.x2)
			return false;
		if (y > this.y && y > this.y2)
			return false;
		return true;
	}
}
