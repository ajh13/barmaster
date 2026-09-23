package com.barmaster;

import org.junit.Test;
import static org.junit.Assert.*;

public class ModernPrayerConfigTest
{
	private final BarMasterConfig config = new BarMasterConfig() { };

	@Test
	public void badgeIsAdditiveAndOffByDefault()
	{
		assertFalse(config.modernPrayerOverheads());
		assertEquals(PrayerBadgePlacement.LEFT, config.prayerBadgePlacement());
		assertTrue(config.prayerBadgeSize() >= 20);
		assertFalse(config.prayerBadgeBorder());
		assertTrue(config.prayerBadgeBackground().getAlpha() > 0);
	}
}
