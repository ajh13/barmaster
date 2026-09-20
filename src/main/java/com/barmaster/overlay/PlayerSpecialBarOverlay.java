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

public class PlayerSpecialBarOverlay extends SingleBarOverlay
{
	@Inject
	PlayerSpecialBarOverlay(Client client, BarMasterConfig config)
	{
		super(client, config, BarType.SPECIAL_ATTACK);
		setPosition(OverlayPosition.TOP_RIGHT);
	}
}
