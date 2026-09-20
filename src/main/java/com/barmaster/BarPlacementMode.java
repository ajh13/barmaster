/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BarPlacementMode
{
	FIXED("Fixed overlays only", true, false),
	OVERHEAD("Above heads only", false, true),
	BOTH("Fixed and above heads", true, true);

	private final String name;
	private final boolean fixedEnabled;
	private final boolean overheadEnabled;

	@Override
	public String toString()
	{
		return name;
	}
}
