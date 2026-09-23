package com.barmaster.overlay;

import com.barmaster.PrayerBadgePlacement;
import java.awt.Point;
import java.awt.Rectangle;

/** Badge placement relative to the complete overhead bar stack. */
final class PrayerBadgeLayout
{
	private PrayerBadgeLayout()
	{
	}

	static int stackHeight(int visibleBars, int barHeight, boolean vertical)
	{
		if (visibleBars <= 0)
		{
			return 0;
		}
		return vertical ? barHeight : visibleBars * barHeight + (visibleBars - 1) * StatusBarOverlay.PADDING;
	}

	static Point position(Rectangle stack, int size, int gap, PrayerBadgePlacement placement)
	{
		switch (placement)
		{
			case LEFT: return new Point(stack.x - gap - size, stack.y);
			case RIGHT: return new Point(stack.x + stack.width + gap, stack.y);
			case ABOVE: return new Point(stack.x + (stack.width - size) / 2, stack.y - gap - size);
			case BELOW: return new Point(stack.x + (stack.width - size) / 2, stack.y + stack.height + gap);
			default: throw new IllegalArgumentException("Unknown badge placement: " + placement);
		}
	}
}
