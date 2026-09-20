/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2026 BarMaster contributors
 */
package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.util.BarType;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPosition;

public class PlayerRunEnergyBarOverlay extends SingleBarOverlay
{
	@Inject
	PlayerRunEnergyBarOverlay(Client client, BarMasterConfig config)
	{
		super(client, config, BarType.RUN_ENERGY);
		setPosition(OverlayPosition.BOTTOM_LEFT);
	}
}
