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
package com.barmaster;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(BarMasterConfig.CONFIG_GROUP)
public interface BarMasterConfig extends Config
{
	String CONFIG_GROUP = "barmaster";

	@ConfigSection(
		name = "Layout",
		description = "Layout and grouping options",
		position = 0
	)
	String layoutSection = "layout";

	@ConfigSection(
		name = "Style",
		description = "Global bar styling options",
		position = 1
	)
	String styleSection = "style";

	@ConfigSection(
		name = "Player Hitpoints",
		description = "Player HP bar options",
		position = 2
	)
	String hpSection = "hitpoints";

	@ConfigSection(
		name = "Player Prayer",
		description = "Player prayer bar options",
		position = 3
	)
	String prayerSection = "prayer";

	@ConfigSection(
		name = "Special Attack",
		description = "Special attack bar options",
		position = 4
	)
	String specialSection = "special";

	@ConfigSection(
		name = "Target Hitpoints",
		description = "Target HP bar options",
		position = 5
	)
	String targetSection = "target";

	@ConfigItem(
		keyName = "groupPlayerBars",
		name = "Group player bars",
		description = "Show HP, prayer and special attack as a single grouped frame instead of independent overlays",
		position = 0,
		section = layoutSection
	)
	default boolean groupPlayerBars()
	{
		return true;
	}

	@Range(min = 20, max = 400)
	@ConfigItem(
		keyName = "barWidth",
		name = "Bar width",
		description = "Width of each status bar in pixels",
		position = 10,
		section = styleSection
	)
	default int barWidth()
	{
		return 120;
	}

	@Range(min = 8, max = 64)
	@ConfigItem(
		keyName = "barHeight",
		name = "Bar height",
		description = "Height of each status bar in pixels",
		position = 11,
		section = styleSection
	)
	default int barHeight()
	{
		return 20;
	}

	@ConfigItem(
		keyName = "showText",
		name = "Show text labels",
		description = "Display current / maximum value text on bars",
		position = 12,
		section = styleSection
	)
	default boolean showText()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPercentage",
		name = "Show percentage",
		description = "Append percentage to bar text labels",
		position = 13,
		section = styleSection
	)
	default boolean showPercentage()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "textColor",
		name = "Text color",
		description = "Color of the text drawn on bars",
		position = 14,
		section = styleSection
	)
	default Color textColor()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		keyName = "backgroundColor",
		name = "Background color",
		description = "Color of the empty portion of each bar",
		position = 15,
		section = styleSection
	)
	default Color backgroundColor()
	{
		return new Color(30, 30, 30, 200);
	}

	@ConfigItem(
		keyName = "showPlayerHp",
		name = "Show player HP bar",
		description = "Display the player's hitpoints bar",
		position = 20,
		section = hpSection
	)
	default boolean showPlayerHp()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "hpColor",
		name = "HP color",
		description = "Fill color of the player HP bar",
		position = 21,
		section = hpSection
	)
	default Color hpColor()
	{
		return new Color(220, 40, 40, 255);
	}

	@ConfigItem(
		keyName = "showPlayerPrayer",
		name = "Show player prayer bar",
		description = "Display the player's prayer bar",
		position = 30,
		section = prayerSection
	)
	default boolean showPlayerPrayer()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "prayerColor",
		name = "Prayer color",
		description = "Fill color of the player prayer bar",
		position = 31,
		section = prayerSection
	)
	default Color prayerColor()
	{
		return new Color(40, 130, 220, 255);
	}

	@ConfigItem(
		keyName = "showSpecialAttack",
		name = "Show special attack bar",
		description = "Display the player's special attack energy bar",
		position = 40,
		section = specialSection
	)
	default boolean showSpecialAttack()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "specialColor",
		name = "Special attack color",
		description = "Fill color of the special attack bar",
		position = 41,
		section = specialSection
	)
	default Color specialColor()
	{
		return new Color(220, 200, 40, 255);
	}

	@ConfigItem(
		keyName = "showTargetHp",
		name = "Show target HP bar",
		description = "Display the current target's HP bar when interacting with an entity",
		position = 50,
		section = targetSection
	)
	default boolean showTargetHp()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "targetHpColor",
		name = "Target HP color",
		description = "Fill color of the target HP bar",
		position = 51,
		section = targetSection
	)
	default Color targetHpColor()
	{
		return new Color(40, 180, 60, 255);
	}
}
