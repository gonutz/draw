package draw;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestRectangle {

	@Test
	public void singlePointHasWidthAndHeightOfOne() {
		Rectangle r = new Rectangle(3, 4, 3, 4);
		assertEquals(1, r.width());
		assertEquals(1, r.height());
	}

	@Test
	public void widthAndHeightArePositive() {
		Rectangle r = new Rectangle(2, 2, 3, 4);
		assertEquals(2, r.width());
		assertEquals(3, r.height());

		r = new Rectangle(3, 4, 2, 2);
		assertEquals(2, r.width());
		assertEquals(3, r.height());
	}

	@Test
	public void leftIsSmallerX() {
		assertEquals(3, new Rectangle(3, 0, 4, 0).left());
		assertEquals(3, new Rectangle(4, 0, 3, 0).left());
	}

	@Test
	public void topIsSmallerY() {
		assertEquals(3, new Rectangle(0, 3, 0, 4).top());
		assertEquals(3, new Rectangle(0, 4, 0, 3).top());
	}

	@Test
	public void movingTranslatesXsAndYs() {
		Rectangle r = new Rectangle(3, 4, 5, 6);
		r.moveBy(1, -2);
		assertEquals(4, r.x);
		assertEquals(2, r.y);
		assertEquals(6, r.x2);
		assertEquals(4, r.y2);
	}
}
