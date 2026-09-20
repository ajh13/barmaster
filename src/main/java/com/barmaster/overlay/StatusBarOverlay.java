/*
 * Copyright (c) 2026, BarMaster Contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.util.BarRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

abstract class StatusBarOverlay extends Overlay
{
	private static final int PADDING = 2;
	private static final int BORDER_SIZE = 1;

	protected final BarMasterConfig config;

	StatusBarOverlay(BarMasterConfig config)
	{
		this.config = config;
		setPosition(OverlayPosition.TOP_CENTER);
		setMovable(true);
		setSnappable(true);
	}

	protected Dimension renderBar(Graphics2D graphics, int x, int y, String name, int current, int max, Color fillColor)
	{
		int width = Math.max(1, config.barWidth());
		int height = Math.max(1, config.barHeight());
		Color background = config.backgroundColor();
		Color text = config.textColor();
		boolean showText = config.showText();
		boolean showPercentage = config.showPercentage();

		String label = showText ? BarRenderer.formatLabel(current, max, showPercentage) : name;

		Rectangle backgroundRect = new Rectangle(x, y, width, height);
		graphics.setColor(background);
		graphics.fill(backgroundRect);

		int fill = BarRenderer.fillWidth(width - 2 * BORDER_SIZE, current, max);
		if (fill > 0)
		{
			Rectangle fillRect = new Rectangle(
				x + BORDER_SIZE,
				y + BORDER_SIZE,
				fill,
				height - 2 * BORDER_SIZE
			);
			graphics.setColor(fillColor);
			graphics.fill(fillRect);
		}

		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(BORDER_SIZE));
		graphics.draw(backgroundRect);

		if (showText || !name.isEmpty())
		{
			FontMetrics metrics = graphics.getFontMetrics();
			int textX = x + (width - metrics.stringWidth(label)) / 2;
			int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
			graphics.setColor(text);
			graphics.drawString(label, textX, textY);
		}

		return new Dimension(width, height + PADDING);
	}
}
