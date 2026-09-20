/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TargetDisplayStyle
{
	RATIO("Ratio / scale"),
	ESTIMATED_HP("Estimated HP when available");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
