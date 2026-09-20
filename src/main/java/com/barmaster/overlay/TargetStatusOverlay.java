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
import com.barmaster.TargetDisplayStyle;
import com.barmaster.TargetSourceMode;
import com.barmaster.util.TargetHpEstimator;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.Text;

public class TargetStatusOverlay extends StatusBarOverlay
{
	private final Client client;
	private final NPCManager npcManager;

	private static final int LAST_TARGET_TIMEOUT_TICKS = 8;

	private Actor lastTarget;
	private int ticksSinceLastTarget;

	@Inject
	TargetStatusOverlay(Client client, BarMasterConfig config, NPCManager npcManager)
	{
		super(config);
		this.client = client;
		this.npcManager = npcManager;
		setPosition(OverlayPosition.BOTTOM_RIGHT);
		setPriority(net.runelite.client.ui.overlay.OverlayPriority.LOW);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Actor target = resolveCurrentTarget();
		if (target != null)
		{
			lastTarget = target;
			ticksSinceLastTarget = 0;
		}
		else if (lastTarget != null && ++ticksSinceLastTarget > LAST_TARGET_TIMEOUT_TICKS)
		{
			lastTarget = null;
		}
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showTargetHp())
		{
			return null;
		}

		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			lastTarget = null;
			ticksSinceLastTarget = 0;
			return null;
		}

		Actor target = resolveTargetForRender();
		if (target == null)
		{
			return null;
		}

		int healthRatio = target.getHealthRatio();
		int healthScale = target.getHealthScale();
		if (healthScale <= 0 || healthRatio < 0)
		{
			return null;
		}

		int maxHp = 0;
		if (target instanceof NPC)
		{
			NPC npc = (NPC) target;
			if (npc.getTransformedComposition() != null)
			{
				Integer health = npcManager.getHealth(npc.getId());
				maxHp = health == null ? 0 : health;
			}
		}

		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(healthRatio, healthScale, maxHp);
		boolean showEstimated = config.targetDisplayStyle() == TargetDisplayStyle.ESTIMATED_HP;
		int current = showEstimated && estimate.isRealHp() ? estimate.getCurrent() : healthRatio;
		int max = showEstimated && estimate.isRealHp() ? estimate.getMax() : healthScale;

		String name = target.getName() == null ? "Target" : Text.removeTags(target.getName());
		return renderBar(graphics, 0, 0, name, current, max, config.targetHpColor(),
			config.targetBarWidth(), config.targetBarHeight());
	}

	private Actor resolveTargetForRender()
	{
		Actor current = resolveCurrentTarget();
		if (config.targetSourceMode() == TargetSourceMode.CURRENT_INTERACTION)
		{
			return current;
		}

		if (current != null)
		{
			return current;
		}

		return lastTarget;
	}

	private Actor resolveCurrentTarget()
	{
		Actor localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return null;
		}

		Actor interacting = localPlayer.getInteracting();
		if (interacting == null || interacting == localPlayer)
		{
			return null;
		}

		return interacting;
	}
}
