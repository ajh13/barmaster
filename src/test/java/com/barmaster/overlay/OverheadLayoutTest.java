package com.barmaster.overlay;

import com.barmaster.OverheadPlacement;
import java.awt.Point;
import java.awt.Rectangle;
import org.junit.Test;
import static org.junit.Assert.*;

public class OverheadLayoutTest
{
	private static final Rectangle NATIVE = new Rectangle(80, 50, 40, 70);

	private Point position(OverheadPlacement side, int width, int height)
	{
		return OverheadLayout.position(100, 100, width, height,
			20, 50, 20, 8, 8, 0, 0, side);
	}

	@Test
	public void stackHeightIncludesRenderedInterBarSpacing()
	{
		assertEquals(86, OverheadLayout.stackHeight(4, 20, false));
		assertEquals(20, OverheadLayout.stackHeight(1, 20, false));
		assertEquals(20, OverheadLayout.stackHeight(4, 20, true));
		assertEquals(0, OverheadLayout.stackHeight(0, 20, false));
	}

	@Test
	public void placesWholeHorizontalStackOutsideNativeUi()
	{
		for (OverheadPlacement side : OverheadPlacement.values())
		{
			Point point = position(side, 120, 48);
			assertFalse(side.toString(), NATIVE.intersects(new Rectangle(point.x, point.y, 120, 48)));
		}
		assertEquals(new Point(-48, 44), position(OverheadPlacement.LEFT, 120, 48));
		assertEquals(new Point(128, 44), position(OverheadPlacement.RIGHT, 120, 48));
		assertEquals(new Point(40, -6), position(OverheadPlacement.ABOVE, 120, 48));
		assertEquals(new Point(40, 128), position(OverheadPlacement.BELOW, 120, 48));
	}

	@Test
	public void placesWholeVerticalStackOutsideNativeUi()
	{
		Point point = position(OverheadPlacement.LEFT, 84, 90);
		assertEquals(new Point(-12, 2), point);
		assertFalse(NATIVE.intersects(new Rectangle(point.x, point.y, 84, 90)));
	}

	@Test
	public void appliesActorOffsetToBothAnchorAndReservedRegion()
	{
		Point point = OverheadLayout.position(100, 100, 40, 20,
			20, 50, 20, 8, 8, 10, -5, OverheadPlacement.RIGHT);
		assertEquals(new Point(138, 67), point);
	}
}
