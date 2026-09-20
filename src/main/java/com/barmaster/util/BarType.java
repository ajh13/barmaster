/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.util;

public enum BarType
{
	HITPOINTS("HP"),
	PRAYER("Prayer"),
	SPECIAL_ATTACK("Special"),
	RUN_ENERGY("Run");

	private final String label;

	BarType(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}
}
