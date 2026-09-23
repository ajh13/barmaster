package com.barmaster.overlay;

import com.barmaster.OverheadPlacement;
import java.awt.Point;

/** Places the entire bar stack outside a conservative native-overhead exclusion region. */
final class OverheadLayout
{
	private OverheadLayout()
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

	static Point position(int anchorX, int anchorY, int stackWidth, int stackHeight,
		int nativeHalfWidth, int nativeTop, int nativeBottom, int sideGap, int verticalGap,
		int offsetX, int offsetY, OverheadPlacement placement)
	{
		int x = anchorX + offsetX;
		int y = anchorY + offsetY;
		switch (placement)
		{
			case LEFT:
				return new Point(x - nativeHalfWidth - sideGap - stackWidth, y - stackHeight - verticalGap);
			case RIGHT:
				return new Point(x + nativeHalfWidth + sideGap, y - stackHeight - verticalGap);
			case ABOVE:
				return new Point(x - stackWidth / 2, y - nativeTop - verticalGap - stackHeight);
			case BELOW:
				return new Point(x - stackWidth / 2, y + nativeBottom + verticalGap);
			default:
				throw new IllegalArgumentException("Unknown overhead placement: " + placement);
		}
	}
}
