/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.BarOrientation;
import com.barmaster.BarTextMode;
import com.barmaster.util.BarRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
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
		boolean vertical = config.barOrientation() == BarOrientation.VERTICAL;
		int configuredWidth = resolveWidth(widthOverride);
		int configuredHeight = resolveHeight(heightOverride);
		int width = vertical ? configuredHeight : configuredWidth;
		int height = vertical ? configuredWidth : configuredHeight;
		int border = Math.max(0, config.borderThickness());
		int inset = Math.min(border, Math.min(width, height) / 2);
		Color background = config.backgroundColor();
		Color text = config.textColor();
		Color borderColor = config.borderColor();
		boolean showText = config.showText();
		int arc = Math.min(8, Math.max(2, height / 3));

		Font originalFont = graphics.getFont();
		Object originalAntialiasing = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Object originalTextAntialiasing = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setFont(originalFont.deriveFont((float) Math.min(config.fontSize(), Math.max(6, height - 4))));

		String valueLabel = BarRenderer.formatLabel(current, max, config.barTextMode());
		String label = config.showBarNames() && !name.isEmpty() ? name + " " + valueLabel : valueLabel;

		RoundRectangle2D.Float outer = new RoundRectangle2D.Float(x, y, width, height, arc, arc);
		graphics.setColor(background);
		graphics.fill(outer);

		int fill = vertical
			? BarRenderer.fillWidth(height - 2 * inset, current, max)
			: BarRenderer.fillWidth(width - 2 * inset, current, max);
		if (fill > 0)
		{
			RoundRectangle2D.Float fillRect = vertical
				? new RoundRectangle2D.Float(
					x + inset,
					y + height - inset - fill,
					width - 2 * inset,
					fill,
					Math.max(0, arc - inset),
					Math.max(0, arc - inset)
				)
				: new RoundRectangle2D.Float(
					x + inset,
					y + inset,
					fill,
					height - 2 * inset,
					Math.max(0, arc - inset),
					Math.max(0, arc - inset)
				);
			graphics.setColor(fillColor);
			graphics.fill(fillRect);
		}

		if (border > 0)
		{
			graphics.setColor(borderColor);
			graphics.setStroke(new BasicStroke(border));
			graphics.draw(outer);
		}

		if (showText && !label.isEmpty())
		{
			FontMetrics metrics = graphics.getFontMetrics();
			String fittedLabel = fitBestLabel(label, valueLabel, current, max, metrics, Math.max(0, width - 4));
			if (!fittedLabel.isEmpty())
			{
				int textX = x + (width - metrics.stringWidth(fittedLabel)) / 2;
				int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
				graphics.setColor(new Color(0, 0, 0, 150));
				graphics.drawString(fittedLabel, textX + 1, textY + 1);
				graphics.setColor(text);
				graphics.drawString(fittedLabel, textX, textY);
			}
		}

		graphics.setFont(originalFont);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, originalAntialiasing);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, originalTextAntialiasing);

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

	private static String fitBestLabel(String label, String valueLabel, int current, int max, FontMetrics metrics, int maxWidth)
	{
		if (maxWidth < 12)
		{
			return "";
		}

		String percentLabel = BarRenderer.formatLabel(current, max, BarTextMode.PERCENT);
		String currentLabel = Integer.toString(current);
		String[] candidates = {label, valueLabel, percentLabel, currentLabel};
		for (String candidate : candidates)
		{
			if (candidate != null && !candidate.isEmpty() && metrics.stringWidth(candidate) <= maxWidth)
			{
				return candidate;
			}
		}

		return "";
	}
}
