package com.barmaster.overlay;

import com.barmaster.BarMasterConfig;
import com.barmaster.OverheadSizingMode;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.HeadIcon;
import org.junit.Test;
import static org.junit.Assert.*;

public class PrayerBadgeOverlayTest
{
	@Test
	public void activePrayerDrawsEvenWhenAllBarsHidden()
	{
		Player player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class[]{Player.class},
			(proxy, method, args) -> {
				if (method.getName().equals("getOverheadIcon")) return HeadIcon.MELEE;
				if (method.getName().equals("getLogicalHeight")) return 100;
				if (method.getName().equals("getCanvasTextLocation")) return new Point(180, args != null && (int) args[2] == 0 ? 140 : 80);
				return null;
			});
		Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class[]{Client.class},
			(proxy, method, args) -> {
				if (method.getName().equals("getGameState")) return GameState.LOGGED_IN;
				if (method.getName().equals("getLocalPlayer")) return player;
				return null;
			});
		BarMasterConfig config = new BarMasterConfig() {
			@Override public OverheadSizingMode overheadSizingMode() { return OverheadSizingMode.CONSTANT_SCREEN_SIZE; }
			@Override public int overheadPlayerOffsetX() { return 10; }
			@Override public int overheadPlayerOffsetY() { return -5; }
			@Override public boolean modernPrayerOverheads() { return true; }
			@Override public boolean showPlayerHp() { return false; }
			@Override public boolean showPlayerPrayer() { return false; }
			@Override public boolean showSpecialAttack() { return false; }
			@Override public boolean showRunEnergy() { return false; }
			@Override public boolean showTargetHp() { return false; }
		};
		BufferedImage image = new BufferedImage(400, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try { new OverheadStatusOverlay(client, config, null).render(graphics); }
		finally { graphics.dispose(); }
		boolean hasBadge = false;
		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				hasBadge |= image.getRGB(x, y) != 0;
			}
		}
		assertTrue("Active prayer badge should render when bars are hidden", hasBadge);
		assertNotEquals("Badge should sit left of the native anchor, with configured offsets", 0, image.getRGB(116, 79));
	}

	@Test
	public void drawsBadgeOnlyWhenOptionEnabled()
	{
		BarMasterConfig disabled = new BarMasterConfig() { };
		BarMasterConfig enabled = new BarMasterConfig()
		{
			@Override
			public boolean modernPrayerOverheads()
			{
				return true;
			}
		};
		BufferedImage image = new BufferedImage(160, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			Rectangle barStack = new Rectangle(60, 40, 50, 24);
			OverheadStatusOverlay.renderPrayerBadge(graphics, disabled, HeadIcon.MELEE, barStack, 1f);
			assertEquals(0, image.getRGB(35, 45));
			OverheadStatusOverlay.renderPrayerBadge(graphics, enabled, HeadIcon.MELEE, barStack, 1f);
			assertNotEquals(0, image.getRGB(35, 45));
			assertEquals(0, image.getRGB(65, 20));
		}
		finally
		{
			graphics.dispose();
		}
	}
}
