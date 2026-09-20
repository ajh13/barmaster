/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.util;

import com.barmaster.BarTextMode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BarRendererTest
{
	@Test
	public void testClamp()
	{
		assertEquals(0, BarRenderer.clamp(-5, 0, 10));
		assertEquals(5, BarRenderer.clamp(5, 0, 10));
		assertEquals(10, BarRenderer.clamp(15, 0, 10));
	}

	@Test
	public void testRatio()
	{
		assertEquals(0.5, BarRenderer.ratio(50, 100), 0.0001);
		assertEquals(0.0, BarRenderer.ratio(0, 100), 0.0001);
		assertEquals(1.0, BarRenderer.ratio(120, 100), 0.0001);
		assertEquals(0.0, BarRenderer.ratio(50, 0), 0.0001);
	}

	@Test
	public void testFillWidth()
	{
		assertEquals(60, BarRenderer.fillWidth(120, 50, 100));
		assertEquals(0, BarRenderer.fillWidth(120, 0, 100));
		assertEquals(120, BarRenderer.fillWidth(120, 100, 100));
		assertEquals(0, BarRenderer.fillWidth(120, 50, 0));
		assertEquals(0, BarRenderer.fillWidth(0, 50, 100));
	}

	@Test
	public void testFormatLabel()
	{
		assertEquals("50 / 100", BarRenderer.formatLabel(50, 100, false));
		assertEquals("50 / 100 (50%)", BarRenderer.formatLabel(50, 100, true));
		assertEquals("0 / 100", BarRenderer.formatLabel(0, 100, false));
		assertEquals("100 / 100 (100%)", BarRenderer.formatLabel(100, 100, true));
	}

	@Test
	public void testFormatLabelClampsPercentage()
	{
		assertEquals("120 / 100 (100%)", BarRenderer.formatLabel(120, 100, true));
		assertEquals("-5 / 100 (0%)", BarRenderer.formatLabel(-5, 100, true));
	}

	@Test
	public void testFormatLabelWithoutPercentage()
	{
		assertEquals("33 / 99", BarRenderer.formatLabel(33, 99, false));
	}

	@Test
	public void testFormatLabelTextModes()
	{
		assertEquals("33/99", BarRenderer.formatLabel(33, 99, BarTextMode.NUMBERS));
		assertEquals("33%", BarRenderer.formatLabel(33, 99, BarTextMode.PERCENT));
		assertEquals("33/99 33%", BarRenderer.formatLabel(33, 99, BarTextMode.BOTH));
	}

}
