package com.barmaster.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import net.runelite.api.HeadIcon;

/** Compact display-only prayer badges; the native icon is not modified. */
final class ModernPrayerBadge
{
	private ModernPrayerBadge()
	{
	}

	static boolean shouldRender(boolean enabled, boolean overheadEnabled, HeadIcon icon)
	{
		return enabled && overheadEnabled && icon != null;
	}

	static String label(HeadIcon icon)
	{
		if (icon == null)
		{
			return "";
		}
		switch (icon)
		{
			case MELEE: return "M";
			case RANGED: return "R";
			case MAGIC: return "W";
			case SMITE: return "S";
			case RETRIBUTION: return "Rt";
			case REDEMPTION: return "Rd";
			case RANGE_MAGE: return "RW";
			case RANGE_MELEE: return "RM";
			case MAGE_MELEE: return "WM";
			case RANGE_MAGE_MELEE: return "RWM";
			case WRATH: return "Wr";
			case SOUL_SPLIT: return "SS";
			case DEFLECT_MELEE: return "DM";
			case DEFLECT_RANGE: return "DR";
			case DEFLECT_MAGE: return "DW";
			default: return "?";
		}
	}

	static void draw(Graphics2D graphics, HeadIcon icon, int x, int y, int size,
		Color background, Color foreground, boolean border)
	{
		String text = label(icon);
		if (text.isEmpty() || size < 1)
		{
			return;
		}
		Graphics2D g = (Graphics2D) graphics.create();
		try
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int radius = Math.min(6, size / 4);
			g.setColor(background);
			g.fillRoundRect(x, y, size, size, radius, radius);
			if (border)
			{
				g.setColor(foreground);
				g.drawRoundRect(x, y, size - 1, size - 1, radius, radius);
			}
			int fontSize = Math.max(8, Math.min(size / 2, size - 4));
			g.setFont(g.getFont().deriveFont(Font.BOLD, (float) fontSize));
			FontMetrics metrics = g.getFontMetrics();
			g.setColor(foreground);
			g.drawString(text, x + (size - metrics.stringWidth(text)) / 2,
				y + (size - metrics.getHeight()) / 2 + metrics.getAscent());
		}
		finally
		{
			g.dispose();
		}
	}
}
