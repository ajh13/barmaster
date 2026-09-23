package com.barmaster.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.HeadIcon;
import org.junit.Test;
import static org.junit.Assert.*;

public class ModernPrayerBadgeTest
{
	@Test
	public void namesEachKnownOverheadPrayer()
	{
		assertEquals("M", ModernPrayerBadge.label(HeadIcon.MELEE));
		assertEquals("R", ModernPrayerBadge.label(HeadIcon.RANGED));
		assertEquals("W", ModernPrayerBadge.label(HeadIcon.MAGIC));
		assertEquals("S", ModernPrayerBadge.label(HeadIcon.SMITE));
		for (HeadIcon icon : HeadIcon.values())
		{
			assertFalse(icon.name(), ModernPrayerBadge.label(icon).isEmpty());
		}
	}

	@Test
	public void onlyRendersActiveOptInOverheadPrayers()
	{
		assertFalse(ModernPrayerBadge.shouldRender(false, true, HeadIcon.MELEE));
		assertFalse(ModernPrayerBadge.shouldRender(true, false, HeadIcon.MELEE));
		assertFalse(ModernPrayerBadge.shouldRender(true, true, null));
		assertTrue(ModernPrayerBadge.shouldRender(true, true, HeadIcon.MELEE));
	}

	@Test
	public void drawsOnlyInsideSquareBadge()
	{
		BufferedImage image = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			ModernPrayerBadge.draw(graphics, HeadIcon.MELEE, 10, 10, 26,
				new Color(20, 30, 40, 220), Color.WHITE, false);
		}
		finally
		{
			graphics.dispose();
		}
		assertEquals(0, image.getRGB(0, 0));
		assertNotEquals(0, image.getRGB(14, 14));
		assertEquals(0, image.getRGB(50, 50));
	}
}
