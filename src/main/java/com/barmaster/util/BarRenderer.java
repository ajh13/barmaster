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
package com.barmaster.util;

import java.awt.Color;

/**
 * Pure Java helper for computing status bar geometry, labels and colors.
 * Contains no RuneLite API references and is suitable for unit testing.
 */
public final class BarRenderer
{
	private BarRenderer()
	{
	}

	/**
	 * Clamps value to the inclusive range [min, max].
	 */
	public static int clamp(int value, int min, int max)
	{
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Computes the fill ratio in the range [0.0, 1.0].
	 */
	public static double ratio(int current, int max)
	{
		if (max <= 0)
		{
			return 0.0;
		}
		return clamp(current, 0, max) / (double) max;
	}

	/**
	 * Computes the filled width for a bar of totalWidth pixels.
	 */
	public static int fillWidth(int totalWidth, int current, int max)
	{
		if (totalWidth <= 0 || max <= 0)
		{
			return 0;
		}
		return (int) Math.round(totalWidth * ratio(current, max));
	}

	/**
	 * Formats a bar label such as "50 / 99" or "50 / 99 (50%)".
	 */
	public static String formatLabel(int current, int max, boolean showPercentage)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(current).append(" / ").append(max);
		if (showPercentage && max > 0)
		{
			builder.append(" (").append((int) Math.round(ratio(current, max) * 100.0)).append("%)");
		}
		return builder.toString();
	}

	/**
	 * Linearly interpolates between two colors. The ratio is clamped to [0.0, 1.0]
	 * and the resulting alpha is taken from {@code from}.
	 */
	public static Color lerpColor(Color from, Color to, double ratio)
	{
		if (from == null || to == null)
		{
			throw new IllegalArgumentException("Colors must not be null");
		}

		double clamped = clamp((int) Math.round(ratio * 100.0), 0, 100) / 100.0;
		int red = (int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * clamped);
		int green = (int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * clamped);
		int blue = (int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * clamped);
		return new Color(red, green, blue, from.getAlpha());
	}
}
