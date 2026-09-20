/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.util;

import lombok.Value;

/**
 * Pure Java helper for estimating a target's real hitpoints from the
 * healthRatio / healthScale values exposed by the RuneLite API.
 *
 * <p>When an NPC's maximum HP is known (via {@code NPCManager}), the estimator
 * uses the same midpoint-of-range logic used by RuneLite's opponent-info
 * overlay. Otherwise it falls back to a ratio out of the health scale.</p>
 */
public final class TargetHpEstimator
{
	private TargetHpEstimator()
	{
	}

	/**
	 * Estimated hitpoint values for a target.
	 */
	@Value
	public static class Estimate
	{
		/** Estimated current hitpoints. */
		int current;

		/** Maximum hitpoints, or healthScale when real max HP is unavailable. */
		int max;

		/** True when {@link #max} is a real NPC max HP, false when it is the health scale. */
		boolean realHp;
	}

	/**
	 * Estimates current and maximum hitpoints from raw ratio/scale values.
	 *
	 * @param healthRatio the actor's health ratio (0 to healthScale inclusive)
	 * @param healthScale the actor's health scale
	 * @param maxHp       the NPC's real max HP, or {@code <= 0} if unknown
	 * @return an estimate; never {@code null}
	 */
	public static Estimate estimate(int healthRatio, int healthScale, int maxHp)
	{
		if (healthScale <= 0)
		{
			return new Estimate(0, 1, false);
		}

		int ratio = BarRenderer.clamp(healthRatio, 0, healthScale);

		if (maxHp > 0)
		{
			if (ratio == 0)
			{
				return new Estimate(0, maxHp, true);
			}

			// Server-side HP ratio is approximately:
			// healthRatio = 1 + (healthScale - 1) * health / maxHp, with 0 as a special dead value.
			// Reverse that to a possible integer HP range and return its midpoint.
			int minHealth = 1;
			if (healthScale > 1 && ratio > 1)
			{
				minHealth = (maxHp * (ratio - 1) + healthScale - 2) / (healthScale - 1);
			}

			int maxHealth = maxHp;
			if (healthScale > 1)
			{
				maxHealth = (maxHp * ratio - 1) / (healthScale - 1);
				maxHealth = Math.min(maxHealth, maxHp);
			}

			if (maxHealth < minHealth)
			{
				maxHealth = minHealth;
			}
			int current = (minHealth + maxHealth + 1) / 2;
			return new Estimate(current, maxHp, true);
		}

		// Fall back to the raw ratio/scale display.
		return new Estimate(ratio, healthScale, false);
	}

}
