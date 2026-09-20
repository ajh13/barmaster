/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster;

import net.runelite.api.gameval.SpriteID;

final class NativeHealthBarSprites
{
	private static final int[] HEALTH = {
		SpriteID.StandardHealth30.FRONT, SpriteID.StandardHealth30.BACK,
		SpriteID.StandardHealth40.FRONT, SpriteID.StandardHealth40.BACK,
		SpriteID.StandardHealth50.FRONT, SpriteID.StandardHealth50.BACK,
		SpriteID.StandardHealth60.FRONT, SpriteID.StandardHealth60.BACK,
		SpriteID.StandardHealth70.FRONT, SpriteID.StandardHealth70.BACK,
		SpriteID.StandardHealth80.FRONT, SpriteID.StandardHealth80.BACK,
		SpriteID.StandardHealth90.FRONT, SpriteID.StandardHealth90.BACK,
		SpriteID.StandardHealth100.FRONT, SpriteID.StandardHealth100.BACK,
		SpriteID.StandardHealth120.FRONT, SpriteID.StandardHealth120.BACK,
		SpriteID.StandardHealth140.FRONT, SpriteID.StandardHealth140.BACK,
		SpriteID.StandardHealth160.FRONT, SpriteID.StandardHealth160.BACK,
	};

	private static final int[] POISON = {
		SpriteID.StandardPoison30.FRONT, SpriteID.StandardPoison30.BACK,
		SpriteID.StandardPoison40.FRONT, SpriteID.StandardPoison40.BACK,
		SpriteID.StandardPoison50.FRONT, SpriteID.StandardPoison50.BACK,
		SpriteID.StandardPoison60.FRONT, SpriteID.StandardPoison60.BACK,
		SpriteID.StandardPoison70.FRONT, SpriteID.StandardPoison70.BACK,
		SpriteID.StandardPoison80.FRONT, SpriteID.StandardPoison80.BACK,
		SpriteID.StandardPoison90.FRONT, SpriteID.StandardPoison90.BACK,
		SpriteID.StandardPoison100.FRONT, SpriteID.StandardPoison100.BACK,
		SpriteID.StandardPoison120.FRONT, SpriteID.StandardPoison120.BACK,
		SpriteID.StandardPoison140.FRONT, SpriteID.StandardPoison140.BACK,
		SpriteID.StandardPoison160.FRONT, SpriteID.StandardPoison160.BACK,
	};

	private static final int[] THEMED_HEALTH = {
		SpriteID.HeadbarBlood120.FRONT, SpriteID.HeadbarBlood120.BACK,
		SpriteID.HeadbarIce120.FRONT, SpriteID.HeadbarIce120.BACK,
		SpriteID.HeadbarHeat120.FRONT, SpriteID.HeadbarHeat120.BACK,
		SpriteID.HeadbarBlood90.FRONT, SpriteID.HeadbarBlood90.BACK,
		SpriteID.HeadbarIce90.FRONT, SpriteID.HeadbarIce90.BACK,
		SpriteID.HeadbarHeat90.FRONT, SpriteID.HeadbarHeat90.BACK,
		SpriteID.HeadbarBlood30.FRONT, SpriteID.HeadbarBlood30.BACK,
	};

	private static final int[] SHOOTING_STAR = {
		SpriteID.HeadbarShootingStar50.FRONT, SpriteID.HeadbarShootingStar50.BACK,
	};

	static final int[] HEALTH_ONLY = concat(HEALTH, POISON, THEMED_HEALTH, SHOOTING_STAR);

	private static int[] concat(int[]... arrays)
	{
		int total = 0;
		for (int[] array : arrays)
		{
			total += array.length;
		}

		int[] result = new int[total];
		int pos = 0;
		for (int[] array : arrays)
		{
			System.arraycopy(array, 0, result, pos, array.length);
			pos += array.length;
		}
		return result;
	}

	private NativeHealthBarSprites()
	{
	}
}
