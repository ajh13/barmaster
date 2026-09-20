/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BarTextMode
{
	NUMBERS("Numbers"),
	PERCENT("Percent"),
	BOTH("Numbers and percent");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
