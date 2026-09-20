/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.BarOrientation;
import com.barmaster.OverheadSizingMode;
import com.barmaster.TargetDisplayStyle;
import com.barmaster.TargetSourceMode;
import com.barmaster.util.TargetHpEstimator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	private static final int OVERHEAD_BAR_GAP = 2;
	private static final double DEFAULT_ACTOR_PIXEL_HEIGHT = 75.0;
	private static final double MIN_OVERHEAD_SCALE = 0.45;
	private static final double MAX_OVERHEAD_SCALE = 2.25;

	private final Client client;
	private final NPCManager npcManager;

	private NPC lastTarget;
	private int ticksSinceLastTarget;
	private List<NPC> nearbyNpcs = Collections.emptyList();
	private List<Player> nearbyPlayers = Collections.emptyList();

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
			nearbyNpcs = Collections.emptyList();
			nearbyPlayers = Collections.emptyList();
			return;
		}

		cacheNearbyHealthActors();

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
		NPC target = resolveTargetForRender();
		renderNearbyHealthBars(graphics, target);
		renderTargetBar(graphics, target);
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
		double scale = overheadScale(graphics, player);
		int barWidth = overheadBarWidth(0, scale);
		int barHeight = overheadBarHeight(0, scale);
		int width = vertical ? barHeight : barWidth;
		int height = vertical ? barWidth : barHeight;
		int gap = scaleOverheadDimension(OVERHEAD_BAR_GAP, scale);
		int overheadGap = overheadGap(scale);
		Point anchor = player.getCanvasTextLocation(graphics, "", player.getLogicalHeight());
		if (anchor == null)
		{
			return;
		}

		int visibleBars = visiblePlayerBarCount();
		if (visibleBars == 0)
		{
			return;
		}

		int stackWidth = visibleBars * width + Math.max(0, visibleBars - 1) * gap;
		int x = vertical
			? anchor.getX() + config.overheadPlayerOffsetX() - stackWidth / 2
			: anchor.getX() + config.overheadPlayerOffsetX() - width / 2;
		int y = vertical
			? anchor.getY() + config.overheadPlayerOffsetY() - overheadGap - height
			: anchor.getY() + config.overheadPlayerOffsetY() - overheadGap - visibleBars * (height + gap);

		if (config.showPlayerHp())
		{
			int current = client.getBoostedSkillLevel(Skill.HITPOINTS);
			int max = client.getRealSkillLevel(Skill.HITPOINTS);
			if (!shouldHideFullBar(current, max))
			{
				Dimension size = renderBar(graphics, x, y, "HP", current, max, config.hpColor(), barWidth, barHeight);
				if (vertical)
				{
					x += size.width + gap;
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
				Dimension size = renderBar(graphics, x, y, "Prayer", current, max, config.prayerColor(), barWidth, barHeight);
				if (vertical)
				{
					x += size.width + gap;
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
				Dimension size = renderBar(graphics, x, y, "Special", current, max, config.specialColor(), barWidth, barHeight);
				if (vertical)
				{
					x += size.width + gap;
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
				Dimension size = renderBar(graphics, x, y, "Run", current, max, config.runEnergyColor(), barWidth, barHeight);
				if (vertical)
				{
					x += size.width + gap;
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

		int maxHp = getNpcMaxHp(target);
		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(healthRatio, healthScale, maxHp);
		boolean showEstimated = config.targetDisplayStyle() == TargetDisplayStyle.ESTIMATED_HP;
		int current = showEstimated && estimate.isRealHp() ? estimate.getCurrent() : healthRatio;
		int max = showEstimated && estimate.isRealHp() ? estimate.getMax() : healthScale;
		String name = target.getName() == null ? "Target" : Text.removeTags(target.getName());
		renderActorHealthBar(graphics, target, name, current, max, config.targetHpColor());
	}

	private void renderNearbyHealthBars(Graphics2D graphics, NPC currentTarget)
	{
		if (config.showNearbyNpcHealthBars())
		{
			for (NPC npc : nearbyNpcs)
			{
				if (npc != currentTarget)
				{
					renderNearbyNpcHealthBar(graphics, npc);
				}
			}
		}

		if (config.showNearbyPlayerHealthBars())
		{
			Player localPlayer = client.getLocalPlayer();
			for (Player player : nearbyPlayers)
			{
				if (player != localPlayer)
				{
					renderNearbyPlayerHealthBar(graphics, player);
				}
			}
		}
	}

	private void cacheNearbyHealthActors()
	{
		if (config.showNearbyNpcHealthBars())
		{
			List<NPC> npcs = new ArrayList<>();
			for (NPC npc : client.getNpcs())
			{
				if (npc != null && npc.getHealthScale() > 0 && npc.getHealthRatio() >= 0)
				{
					npcs.add(npc);
				}
			}
			nearbyNpcs = npcs;
		}
		else
		{
			nearbyNpcs = Collections.emptyList();
		}

		if (config.showNearbyPlayerHealthBars())
		{
			List<Player> players = new ArrayList<>();
			Player localPlayer = client.getLocalPlayer();
			for (Player player : client.getPlayers())
			{
				if (player != null && player != localPlayer && player.getHealthScale() > 0 && player.getHealthRatio() >= 0)
				{
					players.add(player);
				}
			}
			nearbyPlayers = players;
		}
		else
		{
			nearbyPlayers = Collections.emptyList();
		}
	}

	private void renderNearbyNpcHealthBar(Graphics2D graphics, NPC npc)
	{
		if (npc == null || npc.getHealthScale() <= 0 || npc.getHealthRatio() < 0)
		{
			return;
		}

		int maxHp = getNpcMaxHp(npc);
		TargetHpEstimator.Estimate estimate = TargetHpEstimator.estimate(npc.getHealthRatio(), npc.getHealthScale(), maxHp);
		boolean showEstimated = config.targetDisplayStyle() == TargetDisplayStyle.ESTIMATED_HP;
		int current = showEstimated && estimate.isRealHp() ? estimate.getCurrent() : npc.getHealthRatio();
		int max = showEstimated && estimate.isRealHp() ? estimate.getMax() : npc.getHealthScale();
		String name = npc.getName() == null ? "NPC" : Text.removeTags(npc.getName());
		renderActorHealthBar(graphics, npc, name, current, max, config.targetHpColor());
	}

	private void renderNearbyPlayerHealthBar(Graphics2D graphics, Player player)
	{
		if (player == null || player.getHealthScale() <= 0 || player.getHealthRatio() < 0)
		{
			return;
		}

		String name = player.getName() == null ? "Player" : player.getName();
		renderActorHealthBar(graphics, player, name, player.getHealthRatio(), player.getHealthScale(), config.hpColor());
	}

	private void renderActorHealthBar(Graphics2D graphics, Actor actor, String name, int current, int max, java.awt.Color color)
	{
		boolean vertical = config.barOrientation() == BarOrientation.VERTICAL;
		double scale = overheadScale(graphics, actor);
		int barWidth = overheadBarWidth(config.targetBarWidth(), scale);
		int barHeight = overheadBarHeight(config.targetBarHeight(), scale);
		int width = vertical ? barHeight : barWidth;
		int height = vertical ? barWidth : barHeight;
		Point anchor = actor.getCanvasTextLocation(graphics, "", actor.getLogicalHeight());
		if (anchor == null)
		{
			return;
		}

		renderBar(graphics, anchor.getX() - width / 2, anchor.getY() - height - overheadGap(scale), name, current, max,
			color, barWidth, barHeight);
	}

	private int overheadGap(double scale)
	{
		return scaleOverheadDimension(config.overheadGap(), scale);
	}

	private int overheadBarWidth(int widthOverride, double scale)
	{
		return scaleOverheadDimension(resolveWidth(widthOverride), scale);
	}

	private int overheadBarHeight(int heightOverride, double scale)
	{
		return scaleOverheadDimension(resolveHeight(heightOverride), scale);
	}

	private int scaleOverheadDimension(int dimension, double scale)
	{
		return Math.max(1, (int) Math.round(dimension * scale));
	}

	private double overheadScale(Graphics2D graphics, Actor actor)
	{
		if (config.overheadSizingMode() == OverheadSizingMode.CONSTANT_SCREEN_SIZE)
		{
			return 1.0;
		}

		Point bottom = actor.getCanvasTextLocation(graphics, "", 0);
		Point top = actor.getCanvasTextLocation(graphics, "", actor.getLogicalHeight());
		if (bottom == null || top == null)
		{
			return 1.0;
		}

		int apparentHeight = Math.abs(bottom.getY() - top.getY());
		if (apparentHeight <= 0)
		{
			return 1.0;
		}

		return clamp(apparentHeight / DEFAULT_ACTOR_PIXEL_HEIGHT, MIN_OVERHEAD_SCALE, MAX_OVERHEAD_SCALE);
	}

	private static double clamp(double value, double min, double max)
	{
		return Math.max(min, Math.min(max, value));
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
