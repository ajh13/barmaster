package com.barmaster;

import org.junit.Test;
import static org.junit.Assert.*;

public class OverheadPlacementConfigTest
{
	private final BarMasterConfig config = new BarMasterConfig() { };

	@Test
	public void keepsNativeUiClearanceEnabledByDefault()
	{
		assertEquals(OverheadPlacement.LEFT, config.playerOverheadPlacement());
		assertEquals(OverheadPlacement.LEFT, config.npcOverheadPlacement());
		assertTrue(config.nativeOverheadHalfWidth() > 0);
		assertTrue(config.nativeOverheadTop() > 0);
		assertTrue(config.nativeOverheadBottom() > 0);
		assertTrue(config.hideGameCombatBars());
	}
}
