/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.util;

import com.barmaster.BarTextMode;

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

	public static String formatLabel(int current, int max, BarTextMode mode)
	{
		BarTextMode resolvedMode = mode == null ? BarTextMode.NUMBERS : mode;
		int percent = max > 0 ? (int) Math.round(ratio(current, max) * 100.0) : 0;
		switch (resolvedMode)
		{
			case PERCENT:
				return percent + "%";
			case BOTH:
				return current + "/" + max + " " + percent + "%";
			case NUMBERS:
			default:
				return current + "/" + max;
		}
	}

}
