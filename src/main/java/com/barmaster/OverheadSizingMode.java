/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster;

public enum OverheadSizingMode
{
	CONSTANT_SCREEN_SIZE("Constant screen size"),
	SCALE_WITH_CHARACTER("Scale with character");

	private final String displayName;

	OverheadSizingMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
