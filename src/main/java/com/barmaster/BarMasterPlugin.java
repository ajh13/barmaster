/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster;

import com.google.inject.Provides;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.callback.Hooks;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import com.barmaster.overlay.OverheadStatusOverlay;
import com.barmaster.overlay.PlayerHpBarOverlay;
import com.barmaster.overlay.PlayerPrayerBarOverlay;
import com.barmaster.overlay.PlayerRunEnergyBarOverlay;
import com.barmaster.overlay.PlayerSpecialBarOverlay;
import com.barmaster.overlay.PlayerStatusOverlay;
import com.barmaster.overlay.TargetStatusOverlay;

@Slf4j
@PluginDescriptor(
	name = "BarMaster",
	description = "Display-only configurable status bars for HP, prayer, special attack and target HP",
	tags = {"status", "overlay", "hp", "prayer", "special", "target"}
)
public class BarMasterPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;

	@Inject
	private Hooks hooks;

	@Inject
	private BarMasterConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private PlayerStatusOverlay playerStatusOverlay;

	@Inject
	private PlayerHpBarOverlay playerHpBarOverlay;

	@Inject
	private PlayerPrayerBarOverlay playerPrayerBarOverlay;

	@Inject
	private PlayerSpecialBarOverlay playerSpecialBarOverlay;

	@Inject
	private PlayerRunEnergyBarOverlay playerRunEnergyBarOverlay;

	@Inject
	private TargetStatusOverlay targetStatusOverlay;

	@Inject
	private OverheadStatusOverlay overheadStatusOverlay;

	@Inject
	private EventBus eventBus;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDrawNativeRenderable;
	private final Map<Actor, Integer> activeHitsplats = new IdentityHashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		log.debug("BarMaster started!");
		migrateLegacyConfig();
		hooks.registerRenderableDrawListener(drawListener);
		eventBus.register(targetStatusOverlay);
		eventBus.register(overheadStatusOverlay);
		addOverlays();
	}

	@Override
	protected void shutDown() throws Exception
	{
		removeOverlays();
		activeHitsplats.clear();
		eventBus.unregister(targetStatusOverlay);
		eventBus.unregister(overheadStatusOverlay);
		hooks.unregisterRenderableDrawListener(drawListener);
		log.debug("BarMaster stopped!");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!BarMasterConfig.CONFIG_GROUP.equals(event.getGroup()))
		{
			return;
		}

		log.debug("BarMaster config changed: {}", event.getKey());
		removeOverlays();
		addOverlays();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Actor actor = event.getActor();
		Hitsplat hitsplat = event.getHitsplat();
		if (actor != null && hitsplat != null)
		{
			activeHitsplats.put(actor, hitsplat.getDisappearsOnGameCycle());
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		int gameCycle = client.getGameCycle();
		Iterator<Map.Entry<Actor, Integer>> iterator = activeHitsplats.entrySet().iterator();
		while (iterator.hasNext())
		{
			if (iterator.next().getValue() <= gameCycle)
			{
				iterator.remove();
			}
		}
	}

	private void addOverlays()
	{
		if (config.placementMode().isFixedEnabled())
		{
			if (config.groupPlayerBars())
			{
				overlayManager.add(playerStatusOverlay);
			}
			else
			{
				overlayManager.add(playerHpBarOverlay);
				overlayManager.add(playerPrayerBarOverlay);
				overlayManager.add(playerSpecialBarOverlay);
				overlayManager.add(playerRunEnergyBarOverlay);
			}

			overlayManager.add(targetStatusOverlay);
		}

		if (config.placementMode().isOverheadEnabled())
		{
			overlayManager.add(overheadStatusOverlay);
		}
	}

	private void removeOverlays()
	{
		overlayManager.remove(playerStatusOverlay);
		overlayManager.remove(targetStatusOverlay);
		overlayManager.remove(overheadStatusOverlay);
		overlayManager.remove(playerHpBarOverlay);
		overlayManager.remove(playerPrayerBarOverlay);
		overlayManager.remove(playerSpecialBarOverlay);
		overlayManager.remove(playerRunEnergyBarOverlay);
	}

	private boolean shouldDrawNativeRenderable(Renderable renderable, boolean drawingUI)
	{
		if (!drawingUI || !config.hideGameCombatBars())
		{
			return true;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return true;
		}

		if (renderable == localPlayer)
		{
			return hasNativeOverheadUi(localPlayer);
		}

		if (!(renderable instanceof Actor))
		{
			return true;
		}

		Actor actor = (Actor) renderable;
		if (hasVisibleHealth(actor) && shouldHideNativeHealthBarFor(actor, localPlayer))
		{
			return false;
		}
		if (hasNativeOverheadUi(actor))
		{
			return true;
		}

		Actor localInteracting = localPlayer.getInteracting();
		return actor != localInteracting && actor.getInteracting() != localPlayer;
	}

	private boolean shouldHideNativeHealthBarFor(Actor actor, Player localPlayer)
	{
		if (hasNativeOverheadUi(actor))
		{
			return false;
		}

		if (actor instanceof NPC)
		{
			return config.showNearbyNpcHealthBars() || actor == localPlayer.getInteracting() || actor.getInteracting() == localPlayer;
		}

		return actor instanceof Player && config.showNearbyPlayerHealthBars();
	}

	private boolean hasVisibleHealth(Actor actor)
	{
		return actor.getHealthScale() > 0 && actor.getHealthRatio() >= 0;
	}

	private boolean hasNativeOverheadUi(Actor actor)
	{
		if (hasActiveHitsplat(actor))
		{
			return true;
		}

		if (actor.getOverheadText() != null)
		{
			return true;
		}

		if (actor instanceof Player)
		{
			Player player = (Player) actor;
			return player.getOverheadIcon() != null || player.getSkullIcon() != -1;
		}

		if (actor instanceof NPC)
		{
			NPC npc = (NPC) actor;
			return hasAnyOverheadSprite(npc.getOverheadArchiveIds()) || hasAnyOverheadSprite(npc.getOverheadSpriteIds());
		}

		return false;
	}

	private boolean hasAnyOverheadSprite(int[] overheadSprites)
	{
		return overheadSprites != null && overheadSprites.length > 0;
	}

	private boolean hasAnyOverheadSprite(short[] overheadSprites)
	{
		return overheadSprites != null && overheadSprites.length > 0;
	}

	private boolean hasActiveHitsplat(Actor actor)
	{
		Integer expiresOnCycle = activeHitsplats.get(actor);
		return expiresOnCycle != null && expiresOnCycle > client.getGameCycle();
	}

	@Provides
	BarMasterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BarMasterConfig.class);
	}

	private void migrateLegacyConfig()
	{
		String legacyShowPercentage = configManager.getConfiguration(BarMasterConfig.CONFIG_GROUP, "showPercentage");
		String textMode = configManager.getConfiguration(BarMasterConfig.CONFIG_GROUP, "barTextMode");
		if (legacyShowPercentage != null && textMode == null)
		{
			configManager.setConfiguration(BarMasterConfig.CONFIG_GROUP, "barTextMode",
				Boolean.parseBoolean(legacyShowPercentage) ? BarTextMode.BOTH : BarTextMode.NUMBERS);
			configManager.unsetConfiguration(BarMasterConfig.CONFIG_GROUP, "showPercentage");
		}
	}
}
