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
		if (!config.groupPlayerBars())
		{
			return null;
		}

		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return null;
		}

		if (!config.showPlayerHp() && !config.showPlayerPrayer() && !config.showSpecialAttack())
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

		return total.height == 0 ? null : total;
	}

	private boolean shouldHideFullBar(int current, int max)
	{
		return config.hideFullPlayerBars() && max > 0 && current >= max;
	}
}
