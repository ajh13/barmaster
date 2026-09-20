/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster;

import com.google.inject.Provides;
import javax.inject.Inject;
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

	@Override
	protected void startUp() throws Exception
	{
		log.debug("BarMaster started!");
		migrateLegacyConfig();
		eventBus.register(targetStatusOverlay);
		eventBus.register(overheadStatusOverlay);
		addOverlays();
	}

	@Override
	protected void shutDown() throws Exception
	{
		removeOverlays();
		eventBus.unregister(targetStatusOverlay);
		eventBus.unregister(overheadStatusOverlay);
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
