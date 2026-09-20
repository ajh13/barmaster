/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
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

	@ConfigSection(
		name = "Run Energy",
		description = "Run energy bar options",
		position = 6
	)
	String runSection = "run";

	@ConfigItem(
		keyName = "groupPlayerBars",
		name = "Group player bars",
		description = "Stack HP, prayer, special attack, and run energy as one movable overlay instead of independent overlays",
		position = 0,
		section = layoutSection
	)
	default boolean groupPlayerBars()
	{
		return true;
	}

	@ConfigItem(
		keyName = "placementMode",
		name = "Bar placement",
		description = "Show bars as fixed overlays, above character heads, or both",
		position = 1,
		section = layoutSection
	)
	default BarPlacementMode placementMode()
	{
		return BarPlacementMode.FIXED;
	}

	@ConfigItem(
		keyName = "barOrientation",
		name = "Bar orientation",
		description = "Render bars horizontally or vertically",
		position = 2,
		section = layoutSection
	)
	default BarOrientation barOrientation()
	{
		return BarOrientation.HORIZONTAL;
	}

	@Range(min = -300, max = 300)
	@ConfigItem(
		keyName = "overheadPlayerOffsetX",
		name = "Overhead player X offset",
		description = "Move the player overhead bar stack left or right in pixels",
		position = 3,
		section = layoutSection
	)
	default int overheadPlayerOffsetX()
	{
		return 0;
	}

	@Range(min = -300, max = 300)
	@ConfigItem(
		keyName = "overheadPlayerOffsetY",
		name = "Overhead player Y offset",
		description = "Move the player overhead bar stack up or down in pixels; negative moves up",
		position = 4,
		section = layoutSection
	)
	default int overheadPlayerOffsetY()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "overheadSizingMode",
		name = "Overhead sizing",
		description = "Keep overhead bars a constant screen size, or scale them with the character while zooming",
		position = 5,
		section = layoutSection
	)
	default OverheadSizingMode overheadSizingMode()
	{
		return OverheadSizingMode.CONSTANT_SCREEN_SIZE;
	}

	@Range(min = 0, max = 120)
	@ConfigItem(
		keyName = "overheadGap",
		name = "Overhead gap",
		description = "Gap between the top of the character and the overhead bar stack; scales with the character in scale mode",
		position = 6,
		section = layoutSection
	)
	default int overheadGap()
	{
		return 20;
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
		keyName = "barTextMode",
		name = "Text mode",
		description = "Choose whether bar labels show numbers, percentages, or both",
		position = 13,
		section = styleSection
	)
	default BarTextMode barTextMode()
	{
		return BarTextMode.NUMBERS;
	}

	@ConfigItem(
		keyName = "showBarNames",
		name = "Show bar names",
		description = "Prefix labels with HP, Prayer, Special, or target names",
		position = 14,
		section = styleSection
	)
	default boolean showBarNames()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideFullPlayerBars",
		name = "Hide full player bars",
		description = "Do not show player HP, prayer, special attack, or run energy bars when they are at maximum",
		position = 15,
		section = styleSection
	)
	default boolean hideFullPlayerBars()
	{
		return false;
	}

	@Range(min = 8, max = 32)
	@ConfigItem(
		keyName = "fontSize",
		name = "Font size",
		description = "Font size used for bar text labels; labels auto-hide when bars are too skinny",
		position = 16,
		section = styleSection
	)
	default int fontSize()
	{
		return 12;
	}

	@Alpha
	@ConfigItem(
		keyName = "textColor",
		name = "Text color",
		description = "Color of the text drawn on bars",
		position = 17,
		section = styleSection
	)
	default Color textColor()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		keyName = "borderColor",
		name = "Border color",
		description = "Color of the border drawn around each bar",
		position = 18,
		section = styleSection
	)
	default Color borderColor()
	{
		return Color.BLACK;
	}

	@Range(min = 0, max = 5)
	@ConfigItem(
		keyName = "borderThickness",
		name = "Border thickness",
		description = "Thickness of the border drawn around each bar in pixels",
		position = 19,
		section = styleSection
	)
	default int borderThickness()
	{
		return 0;
	}

	@Alpha
	@ConfigItem(
		keyName = "backgroundColor",
		name = "Background color",
		description = "Color of the empty portion of each bar",
		position = 20,
		section = styleSection
	)
	default Color backgroundColor()
	{
		return new Color(18, 18, 18, 230);
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
		return new Color(60, 210, 95, 255);
	}

	@ConfigItem(
		keyName = "showRunEnergy",
		name = "Show run energy bar",
		description = "Display the player's run energy bar",
		position = 45,
		section = runSection
	)
	default boolean showRunEnergy()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "runEnergyColor",
		name = "Run energy color",
		description = "Fill color of the run energy bar",
		position = 46,
		section = runSection
	)
	default Color runEnergyColor()
	{
		return new Color(235, 125, 35, 255);
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

	@ConfigItem(
		keyName = "targetDisplayStyle",
		name = "Target display style",
		description = "Show raw ratio/scale or estimated real HP when NPC max HP is known",
		position = 52,
		section = targetSection
	)
	default TargetDisplayStyle targetDisplayStyle()
	{
		return TargetDisplayStyle.ESTIMATED_HP;
	}

	@ConfigItem(
		keyName = "targetSourceMode",
		name = "Target source mode",
		description = "Use the current interacting target only, or remember the last valid target",
		position = 53,
		section = targetSection
	)
	default TargetSourceMode targetSourceMode()
	{
		return TargetSourceMode.CURRENT_INTERACTION;
	}

	@Range(min = 0, max = 400)
	@ConfigItem(
		keyName = "targetBarWidth",
		name = "Target bar width",
		description = "Override the width of the target HP bar (0 to use the global bar width)",
		position = 54,
		section = targetSection
	)
	default int targetBarWidth()
	{
		return 0;
	}

	@Range(min = 0, max = 64)
	@ConfigItem(
		keyName = "targetBarHeight",
		name = "Target bar height",
		description = "Override the height of the target HP bar (0 to use the global bar height)",
		position = 55,
		section = targetSection
	)
	default int targetBarHeight()
	{
		return 0;
	}
}
