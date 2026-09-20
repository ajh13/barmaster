/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.util.BarType;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;

public class SingleBarOverlay extends StatusBarOverlay
{
	private final Client client;
	private final BarType barType;

	SingleBarOverlay(Client client, BarMasterConfig config, BarType barType)
	{
		super(config);
		this.client = client;
		this.barType = barType;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.placementMode().isFixedEnabled())
		{
			return null;
		}

		if (config.groupPlayerBars())
		{
			return null;
		}

		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return null;
		}

		if (!isEnabled())
		{
			return null;
		}

		int current;
		int max;
		String name;
		Color color;

		switch (barType)
		{
			case HITPOINTS:
				current = client.getBoostedSkillLevel(Skill.HITPOINTS);
				max = client.getRealSkillLevel(Skill.HITPOINTS);
				name = "HP";
				color = config.hpColor();
				break;
			case PRAYER:
				current = client.getBoostedSkillLevel(Skill.PRAYER);
				max = client.getRealSkillLevel(Skill.PRAYER);
				name = "Prayer";
				color = config.prayerColor();
				break;
			case SPECIAL_ATTACK:
				int specialTenths = client.getVarpValue(VarPlayerID.SA_ENERGY);
				current = specialTenths / 10;
				max = 100;
				name = "Special";
				color = config.specialColor();
				break;
			case RUN_ENERGY:
				current = (int) Math.round(client.getEnergy() / 100.0);
				max = 100;
				name = "Run";
				color = config.runEnergyColor();
				break;
			default:
				return null;
		}

		if (config.hideFullPlayerBars() && max > 0 && current >= max)
		{
			return null;
		}

		return renderBar(graphics, 0, 0, name, current, max, color);
	}

	private boolean isEnabled()
	{
		switch (barType)
		{
			case HITPOINTS:
				return config.showPlayerHp();
			case PRAYER:
				return config.showPlayerPrayer();
			case SPECIAL_ATTACK:
				return config.showSpecialAttack();
			case RUN_ENERGY:
				return config.showRunEnergy();
			default:
				return false;
		}
	}
}
