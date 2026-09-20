/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TargetHpEstimatorTest
{
	@Test
	public void estimatesRealHpFromKnownNpcMaxHealth()
	{
		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(15, 30, 100);

		assertEquals(50, estimate.getCurrent());
		assertEquals(100, estimate.getMax());
		assertTrue(estimate.isRealHp());
	}

	@Test
	public void returnsZeroForDeadKnownNpc()
	{
		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(0, 30, 100);

		assertEquals(0, estimate.getCurrent());
		assertEquals(100, estimate.getMax());
		assertTrue(estimate.isRealHp());
	}

	@Test
	public void fallsBackToRatioScaleWhenMaxHealthUnknown()
	{
		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(12, 30, 0);

		assertEquals(12, estimate.getCurrent());
		assertEquals(30, estimate.getMax());
		assertFalse(estimate.isRealHp());
	}

	@Test
	public void clampsRatioBeforeEstimating()
	{
		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(99, 30, 100);

		assertEquals(100, estimate.getCurrent());
		assertEquals(100, estimate.getMax());
		assertTrue(estimate.isRealHp());
	}
}
