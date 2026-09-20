/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.TargetDisplayStyle;
import com.barmaster.TargetSourceMode;
import com.barmaster.util.TargetHpEstimator;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.Text;

public class TargetStatusOverlay extends StatusBarOverlay
{
	private final Client client;
	private final NPCManager npcManager;

	private static final int LAST_TARGET_TIMEOUT_TICKS = 8;

	private NPC lastTarget;
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
		if (!config.placementMode().isFixedEnabled() || !config.showTargetHp())
		{
			lastTarget = null;
			ticksSinceLastTarget = 0;
			return;
		}

		NPC target = resolveCurrentTarget();
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

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc() == lastTarget)
		{
			lastTarget = null;
			ticksSinceLastTarget = 0;
		}
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.placementMode().isFixedEnabled())
		{
			return null;
		}

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

		NPC target = resolveTargetForRender();
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
		if (target.getTransformedComposition() != null)
		{
			Integer health = npcManager.getHealth(target.getTransformedComposition().getId());
			maxHp = health == null ? 0 : health;
		}

		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(healthRatio, healthScale, maxHp);
		boolean showEstimated = config.targetDisplayStyle() == TargetDisplayStyle.ESTIMATED_HP;
		int current = showEstimated && estimate.isRealHp() ? estimate.getCurrent() : healthRatio;
		int max = showEstimated && estimate.isRealHp() ? estimate.getMax() : healthScale;

		String name = target.getName() == null ? "Target" : Text.removeTags(target.getName());
		return renderBar(graphics, 0, 0, name, current, max, config.targetHpColor(),
			config.targetBarWidth(), config.targetBarHeight());
	}

	private NPC resolveTargetForRender()
	{
		NPC current = resolveCurrentTarget();
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

	private NPC resolveCurrentTarget()
	{
		var localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return null;
		}

		var interacting = localPlayer.getInteracting();
		if (!(interacting instanceof NPC))
		{
			return null;
		}

		return (NPC) interacting;
	}
}
