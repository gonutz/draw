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

	public int left() {
		if (x < x2)
			return x;
		return x2;
	}

	public int top() {
		if (y < y2)
			return y;
		return y2;
	}

	public int width() {
		int dx = x - x2;
		if (dx >= 0)
			return dx + 1;
		return -dx + 1;
	}

	public int height() {
		int dy = y - y2;
		if (dy >= 0)
			return dy + 1;
		return -dy + 1;
	}

	public void moveBy(int dx, int dy) {
		x += dx;
		x2 += dx;
		y += dy;
		y2 += dy;
	}

	@Override
	public String toString() {
		return String.format("(%d,%d)-(%d,%d)", x, y, x2, y2);
	}
}
