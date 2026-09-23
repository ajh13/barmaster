package com.barmaster.overlay;

import com.barmaster.PrayerBadgePlacement;
import java.awt.Point;
import java.awt.Rectangle;
import org.junit.Test;
import static org.junit.Assert.*;

public class PrayerBadgeLayoutTest
{
	@Test
	public void renderedStackHeightAccountsForInterBarSpacing()
	{
		assertEquals(86, PrayerBadgeLayout.stackHeight(4, 20, false));
		assertEquals(20, PrayerBadgeLayout.stackHeight(1, 20, false));
		assertEquals(20, PrayerBadgeLayout.stackHeight(4, 20, true));
	}

	@Test
	public void positionsOutsideEntireBarStack()
	{
		Rectangle stack = new Rectangle(50, 60, 100, 35);
		for (PrayerBadgePlacement placement : PrayerBadgePlacement.values())
		{
			Point badge = PrayerBadgeLayout.position(stack, 24, 5, placement);
			assertFalse(placement.toString(), stack.intersects(new Rectangle(badge.x, badge.y, 24, 24)));
		}
		assertEquals(new Point(21, 60), PrayerBadgeLayout.position(stack, 24, 5, PrayerBadgePlacement.LEFT));
		assertEquals(new Point(155, 60), PrayerBadgeLayout.position(stack, 24, 5, PrayerBadgePlacement.RIGHT));
		assertEquals(new Point(88, 31), PrayerBadgeLayout.position(stack, 24, 5, PrayerBadgePlacement.ABOVE));
		assertEquals(new Point(88, 100), PrayerBadgeLayout.position(stack, 24, 5, PrayerBadgePlacement.BELOW));
	}
}
