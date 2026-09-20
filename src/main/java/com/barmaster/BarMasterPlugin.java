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
import com.barmaster.overlay.PlayerHpBarOverlay;
import com.barmaster.overlay.PlayerPrayerBarOverlay;
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
	private PlayerStatusOverlay playerStatusOverlay;

	@Inject
	private PlayerHpBarOverlay playerHpBarOverlay;

	@Inject
	private PlayerPrayerBarOverlay playerPrayerBarOverlay;

	@Inject
	private PlayerSpecialBarOverlay playerSpecialBarOverlay;

	@Inject
	private TargetStatusOverlay targetStatusOverlay;

	@Inject
	private EventBus eventBus;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("BarMaster started!");
		eventBus.register(targetStatusOverlay);
		addOverlays();
	}

	@Override
	protected void shutDown() throws Exception
	{
		removeOverlays();
		eventBus.unregister(targetStatusOverlay);
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
	}

	private void addOverlays()
	{
		overlayManager.add(playerStatusOverlay);
		overlayManager.add(targetStatusOverlay);
		overlayManager.add(playerHpBarOverlay);
		overlayManager.add(playerPrayerBarOverlay);
		overlayManager.add(playerSpecialBarOverlay);
	}

	private void removeOverlays()
	{
		overlayManager.remove(playerStatusOverlay);
		overlayManager.remove(targetStatusOverlay);
		overlayManager.remove(playerHpBarOverlay);
		overlayManager.remove(playerPrayerBarOverlay);
		overlayManager.remove(playerSpecialBarOverlay);
	}

	@Provides
	BarMasterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BarMasterConfig.class);
	}
}
