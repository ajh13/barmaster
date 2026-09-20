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
	public void testFormatLabelWithoutPercentage()
	{
		assertEquals("33 / 99", BarRenderer.formatLabel(33, 99, false));
	}

	@Test
	public void testLerpColor()
	{
		Color black = new Color(0, 0, 0, 255);
		Color white = new Color(255, 255, 255, 255);

		Color mid = BarRenderer.lerpColor(black, white, 0.5);
		assertEquals(128, mid.getRed());
		assertEquals(128, mid.getGreen());
		assertEquals(128, mid.getBlue());
		assertEquals(255, mid.getAlpha());

		Color start = BarRenderer.lerpColor(black, white, 0.0);
		assertEquals(0, start.getRed());

		Color end = BarRenderer.lerpColor(black, white, 1.0);
		assertEquals(255, end.getRed());

		Color clamped = BarRenderer.lerpColor(black, white, 2.0);
		assertEquals(255, clamped.getRed());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLerpColorNullFrom()
	{
		BarRenderer.lerpColor(null, Color.BLACK, 0.5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLerpColorNullTo()
	{
		BarRenderer.lerpColor(Color.BLACK, null, 0.5);
	}
}
