package draw;

public class Bresenham {

	public static int[] linePoints(int x, int y, int x2, int y2) {
		if (x2 < x) {
			return linePoints(x2, y2, x, y);
		}
		int w = x2 - x;
		int h = y2 - y;
		int lineLength = max(abs(w), abs(h)) + 1;
		int[] points = new int[2 * lineLength];
		int index = 0;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0)
			dx1 = -1;
		else if (w > 0)
			dx1 = 1;
		if (h < 0)
			dy1 = -1;
		else if (h > 0)
			dy1 = 1;
		if (w < 0)
			dx2 = -1;
		else if (w > 0)
			dx2 = 1;
		int longest = abs(w);
		int shortest = abs(h);
		if (!(longest > shortest)) {
			longest = abs(h);
			shortest = abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest / 2;
		for (int i = 0; i <= longest; i++) {
			points[index] = x;
			points[index + 1] = y;
			index += 2;
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}
		return points;
	}

	private static int max(int a, int b) {
		if (a > b)
			return a;
		return b;
	}

	private static int abs(int x) {
		if (x < 0)
			return -x;
		return x;
	}
}
