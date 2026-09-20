/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.BarOrientation;
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
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.Text;

public class OverheadStatusOverlay extends StatusBarOverlay
{
	private static final int LAST_TARGET_TIMEOUT_TICKS = 8;
	private static final int ACTOR_TEXT_OFFSET = 20;

	private final Client client;
	private final NPCManager npcManager;

	private NPC lastTarget;
	private int ticksSinceLastTarget;

	@Inject
	OverheadStatusOverlay(Client client, BarMasterConfig config, NPCManager npcManager)
	{
		super(config);
		this.client = client;
		this.npcManager = npcManager;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGH);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!config.placementMode().isOverheadEnabled() || !config.showTargetHp())
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
		if (!config.placementMode().isOverheadEnabled())
		{
			return null;
		}

		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			lastTarget = null;
			ticksSinceLastTarget = 0;
			return null;
		}

		renderPlayerBars(graphics, client.getLocalPlayer());
		renderTargetBar(graphics, resolveTargetForRender());
		return null;
	}

	private void renderPlayerBars(Graphics2D graphics, Player player)
	{
		int barCount = 0;
		if (config.showPlayerHp())
		{
			barCount++;
		}
		if (config.showPlayerPrayer())
		{
			barCount++;
		}
		if (config.showSpecialAttack())
		{
			barCount++;
		}
		if (config.showRunEnergy())
		{
			barCount++;
		}
		if (barCount == 0)
		{
			return;
		}

		boolean vertical = config.barOrientation() == BarOrientation.VERTICAL;
		int width = vertical ? resolveHeight(0) : resolveWidth(0);
		int height = vertical ? resolveWidth(0) : resolveHeight(0);
		Point anchor = player.getCanvasTextLocation(graphics, "", player.getLogicalHeight() + ACTOR_TEXT_OFFSET);
		if (anchor == null)
		{
			return;
		}

		int visibleBars = visiblePlayerBarCount();
		if (visibleBars == 0)
		{
			return;
		}

		int stackWidth = visibleBars * width + Math.max(0, visibleBars - 1) * 2;
		int x = vertical
			? anchor.getX() + config.overheadPlayerOffsetX() - stackWidth / 2
			: anchor.getX() + config.overheadPlayerOffsetX() - width / 2;
		int y = vertical
			? anchor.getY() + config.overheadPlayerOffsetY() - height
			: anchor.getY() + config.overheadPlayerOffsetY() - visibleBars * (height + 2);

		if (config.showPlayerHp())
		{
			int current = client.getBoostedSkillLevel(Skill.HITPOINTS);
			int max = client.getRealSkillLevel(Skill.HITPOINTS);
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "HP", current, max, config.hpColor());
				if (vertical)
				{
					x += size.width + 2;
				}
				else
				{
					y += size.height;
				}
			}
		}
		if (config.showPlayerPrayer())
		{
			int current = client.getBoostedSkillLevel(Skill.PRAYER);
			int max = client.getRealSkillLevel(Skill.PRAYER);
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "Prayer", current, max, config.prayerColor());
				if (vertical)
				{
					x += size.width + 2;
				}
				else
				{
					y += size.height;
				}
			}
		}
		if (config.showSpecialAttack())
		{
			int current = client.getVarpValue(VarPlayerID.SA_ENERGY) / 10;
			int max = 100;
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "Special", current, max, config.specialColor());
				if (vertical)
				{
					x += size.width + 2;
				}
				else
				{
					y += size.height;
				}
			}
		}
		if (config.showRunEnergy())
		{
			int current = (int) Math.round(client.getEnergy() / 100.0);
			int max = 100;
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "Run", current, max, config.runEnergyColor());
				if (vertical)
				{
					x += size.width + 2;
				}
				else
				{
					y += size.height;
				}
			}
		}
	}

	private int visiblePlayerBarCount()
	{
		int count = 0;
		if (config.showPlayerHp() && !shouldHideFullBar(client.getBoostedSkillLevel(Skill.HITPOINTS), client.getRealSkillLevel(Skill.HITPOINTS)))
		{
			count++;
		}
		if (config.showPlayerPrayer() && !shouldHideFullBar(client.getBoostedSkillLevel(Skill.PRAYER), client.getRealSkillLevel(Skill.PRAYER)))
		{
			count++;
		}
		if (config.showSpecialAttack() && !shouldHideFullBar(client.getVarpValue(VarPlayerID.SA_ENERGY) / 10, 100))
		{
			count++;
		}
		if (config.showRunEnergy() && !shouldHideFullBar((int) Math.round(client.getEnergy() / 100.0), 100))
		{
			count++;
		}
		return count;
	}

	private void renderTargetBar(Graphics2D graphics, NPC target)
	{
		if (!config.showTargetHp() || target == null)
		{
			return;
		}

		int healthRatio = target.getHealthRatio();
		int healthScale = target.getHealthScale();
		if (healthScale <= 0 || healthRatio < 0)
		{
			return;
		}

		boolean vertical = config.barOrientation() == BarOrientation.VERTICAL;
		int width = vertical ? resolveHeight(config.targetBarHeight()) : resolveWidth(config.targetBarWidth());
		int height = vertical ? resolveWidth(config.targetBarWidth()) : resolveHeight(config.targetBarHeight());
		Point anchor = target.getCanvasTextLocation(graphics, "", target.getLogicalHeight() + ACTOR_TEXT_OFFSET);
		if (anchor == null)
		{
			return;
		}

		int maxHp = getNpcMaxHp(target);
		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(healthRatio, healthScale, maxHp);
		boolean showEstimated = config.targetDisplayStyle() == TargetDisplayStyle.ESTIMATED_HP;
		int current = showEstimated && estimate.isRealHp() ? estimate.getCurrent() : healthRatio;
		int max = showEstimated && estimate.isRealHp() ? estimate.getMax() : healthScale;
		String name = target.getName() == null ? "Target" : Text.removeTags(target.getName());

		renderBar(graphics, anchor.getX() - width / 2, anchor.getY() - height - 2, name, current, max, config.targetHpColor(),
			config.targetBarWidth(), config.targetBarHeight());
	}

	private int getNpcMaxHp(NPC npc)
	{
		if (npc.getTransformedComposition() == null)
		{
			return 0;
		}

		Integer health = npcManager.getHealth(npc.getTransformedComposition().getId());
		return health == null ? 0 : health;
	}

	private NPC resolveTargetForRender()
	{
		NPC current = resolveCurrentTarget();
		if (config.targetSourceMode() == TargetSourceMode.CURRENT_INTERACTION)
		{
			return current;
		}

		return current != null ? current : lastTarget;
	}

	private NPC resolveCurrentTarget()
	{
		Actor localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return null;
		}

		Actor interacting = localPlayer.getInteracting();
		return interacting instanceof NPC ? (NPC) interacting : null;
	}

	private boolean shouldHideFullBar(int current, int max)
	{
		return config.hideFullPlayerBars() && max > 0 && current >= max;
	}
}
