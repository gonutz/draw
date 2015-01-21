package draw;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

public class TestBresenhamLine {

	private void assertLinePoints(Point from, Point to, Point... expectedPoints) {
		int[] expected = new int[expectedPoints.length * 2];
		for (int i = 0; i < expectedPoints.length; i++) {
			expected[i * 2] = expectedPoints[i].x;
			expected[i * 2 + 1] = expectedPoints[i].y;
		}
		assertArrayEquals(expected,
				Bresenham.linePoints(from.x, from.y, to.x, to.y));
	}

	private Point from(int x, int y) {
		return new Point(x, y);
	}

	private Point to(int x, int y) {
		return new Point(x, y);
	}

	private Point p(int x, int y) {
		return new Point(x, y);
	}

	@Test
	public void sameStartAndEndPoint_YieldOnePointLine() {
		assertLinePoints(from(0, 0), to(0, 0), p(0, 0));
		assertLinePoints(from(4, 5), to(4, 5), p(4, 5));
	}

	@Test
	public void adjacentPoints_FormTwoPointLine() {
		assertLinePoints(from(0, 0), to(1, 1), p(0, 0), p(1, 1));
		assertLinePoints(from(1, 2), to(0, 1), p(1, 2), p(0, 1));
	}

	@Test
	public void diagonalLines() {
		assertLinePoints(from(0, 0), to(2, 2), p(0, 0), p(1, 1), p(2, 2));
		assertLinePoints(from(0, 2), to(2, 0), p(0, 2), p(1, 1), p(2, 0));
		assertLinePoints(from(4, 5), to(2, 3), p(4, 5), p(3, 4), p(2, 3));
		assertLinePoints(from(10, 20), to(7, 17), p(10, 20), p(9, 19),
				p(8, 18), p(7, 17));
	}

	@Test
	public void nonDiagonalLines() {
		assertLinePoints(from(0, 0), to(3, 1), p(0, 0), p(1, 0), p(2, 1),
				p(3, 1));
		assertLinePoints(from(3, 1), to(0, 0), p(3, 1), p(2, 1), p(1, 0),
				p(0, 0));
	}
}
