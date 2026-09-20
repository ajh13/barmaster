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
			default:
				return false;
		}
	}
}
