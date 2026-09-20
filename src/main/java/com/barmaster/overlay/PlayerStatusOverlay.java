/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;

public class PlayerStatusOverlay extends StatusBarOverlay
{
	private final Client client;

	@Inject
	PlayerStatusOverlay(Client client, BarMasterConfig config)
	{
		super(config);
		this.client = client;
		setPriority(net.runelite.client.ui.overlay.OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.placementMode().isFixedEnabled())
		{
			return null;
		}

		if (!config.groupPlayerBars())
		{
			return null;
		}

		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return null;
		}

		if (!config.showPlayerHp() && !config.showPlayerPrayer() && !config.showSpecialAttack() && !config.showRunEnergy())
		{
			return null;
		}

		int x = 0;
		int y = 0;
		Dimension total = new Dimension(config.barWidth(), 0);

		if (config.showPlayerHp())
		{
			int current = client.getBoostedSkillLevel(Skill.HITPOINTS);
			int max = client.getRealSkillLevel(Skill.HITPOINTS);
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "HP", current, max, config.hpColor());
				y += size.height;
				total.height += size.height;
			}
		}

		if (config.showPlayerPrayer())
		{
			int current = client.getBoostedSkillLevel(Skill.PRAYER);
			int max = client.getRealSkillLevel(Skill.PRAYER);
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "Prayer", current, max, config.prayerColor());
				y += size.height;
				total.height += size.height;
			}
		}

		if (config.showSpecialAttack())
		{
			int specialTenths = client.getVarpValue(VarPlayerID.SA_ENERGY);
			int current = specialTenths / 10;
			int max = 100;
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "Special", current, max, config.specialColor());
				y += size.height;
				total.height += size.height;
			}
		}

		if (config.showRunEnergy())
		{
			int current = (int) Math.round(client.getEnergy() / 100.0);
			int max = 100;
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "Run", current, max, config.runEnergyColor());
				y += size.height;
				total.height += size.height;
			}
		}

		return total.height == 0 ? null : total;
	}

	private boolean shouldHideFullBar(int current, int max)
	{
		return config.hideFullPlayerBars() && max > 0 && current >= max;
	}
}
