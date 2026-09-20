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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

abstract class StatusBarOverlay extends Overlay
{
	private static final int PADDING = 2;

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
		return renderBar(graphics, x, y, name, current, max, fillColor, 0, 0);
	}

	protected Dimension renderBar(Graphics2D graphics, int x, int y, String name, int current, int max, Color fillColor,
								  int widthOverride, int heightOverride)
	{
		int width = resolveWidth(widthOverride);
		int height = resolveHeight(heightOverride);
		int border = Math.max(0, config.borderThickness());
		int inset = Math.min(border, Math.min(width, height) / 2);
		Color background = config.backgroundColor();
		Color text = config.textColor();
		Color borderColor = config.borderColor();
		boolean showText = config.showText();
		boolean showPercentage = config.showPercentage();

		Font originalFont = graphics.getFont();
		graphics.setFont(originalFont.deriveFont((float) Math.min(config.fontSize(), Math.max(8, height - 2))));

		String label = showText ? name + " " + BarRenderer.formatLabel(current, max, showPercentage) : name;

		Rectangle backgroundRect = new Rectangle(x, y, width, height);
		graphics.setColor(background);
		graphics.fill(backgroundRect);

		int fill = BarRenderer.fillWidth(width - 2 * inset, current, max);
		if (fill > 0)
		{
			Rectangle fillRect = new Rectangle(
				x + inset,
				y + inset,
				fill,
				height - 2 * inset
			);
			graphics.setColor(fillColor);
			graphics.fill(fillRect);
		}

		graphics.setColor(borderColor);
		graphics.setStroke(new BasicStroke(border));
		graphics.draw(backgroundRect);

		if (showText || !name.isEmpty())
		{
			FontMetrics metrics = graphics.getFontMetrics();
			int textX = x + (width - metrics.stringWidth(label)) / 2;
			int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
			graphics.setColor(text);
			graphics.drawString(label, textX, textY);
		}

		graphics.setFont(originalFont);

		return new Dimension(width, height + PADDING);
	}

	protected int resolveWidth(int widthOverride)
	{
		return Math.max(1, widthOverride > 0 ? widthOverride : config.barWidth());
	}

	protected int resolveHeight(int heightOverride)
	{
		return Math.max(1, heightOverride > 0 ? heightOverride : config.barHeight());
	}
}
