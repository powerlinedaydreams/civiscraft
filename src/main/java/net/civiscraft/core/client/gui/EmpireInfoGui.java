package net.civiscraft.core.client.gui;

import net.civiscraft.core.CCCore;
import net.civiscraft.core.client.ClientDisplayData;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EmpireInfoGui extends GuiScreen
{
	private static float topBarOffset = 0;

	private static final ResourceLocation TOPBAR_TEX_PATH = new ResourceLocation(CCCore.MODID,
			"textures/gui/topbar.png");
	private static final ResourceLocation FLAG_TEX_PATH = new ResourceLocation(CCCore.MODID, "textures/gui/flag.png");
	private static final ResourceLocation FLAG_SHADOW_TEX_PATH = new ResourceLocation(CCCore.MODID,
			"textures/gui/flagshadow.png");
	private static final ResourceLocation FOOD_PANE_TEX_PATH = new ResourceLocation(CCCore.MODID,
			"textures/gui/foodpane.png");

	public EmpireInfoGui(Minecraft minecraft)
	{
		ScaledResolution sr = new ScaledResolution(minecraft);
		int width = sr.getScaledWidth();
		int height = sr.getScaledHeight();

		GlStateManager.pushMatrix();
		int scale = height / minecraft.displayHeight;
		//GlStateManager.scale(scale, scale, 1.0f);

		minecraft.renderEngine.bindTexture(TOPBAR_TEX_PATH);
		int i;
		for (i = 49; i < width - 104; i += 100)
		{
			drawModalRectWithCustomSizedTexture(i, 0, 0, 19, 100, 19, 148, 38);
		}
		int remainder = width - 4 - i;
		drawModalRectWithCustomSizedTexture(i, 0, 0, 19, remainder, 19, 148, 38);

		drawModalRectWithCustomSizedTexture((int) (49 - topBarOffset), 0, 104, 19, 42, 19, 148, 38);

		for (i = 54; i < width - 94; i += 87)
		{
			drawModalRectWithCustomSizedTexture(i, 0, 54, 0, 87, 19, 148, 38);
		}
		remainder = width - i;
		drawModalRectWithCustomSizedTexture(i, 0, 148 - remainder, 0, remainder, 19, 148, 38);

		//left bit
		drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 54, 19, 148, 38);

		minecraft.renderEngine.bindTexture(FLAG_TEX_PATH);
		//drawModalRectWithCustomSizedTexture(4, 4, 0, 0, 39, 26, 48, 32);

		minecraft.renderEngine.bindTexture(FLAG_SHADOW_TEX_PATH);
		//drawModalRectWithCustomSizedTexture(4, 4, 0, 0, 39, 26, 42, 28);

		GlStateManager.popMatrix();
		int x = minecraft.player.chunkCoordX;
		int z = minecraft.player.chunkCoordZ;
		TilePos tp = new TilePos(new ChunkPos(x, z));

		drawCenteredString(minecraft.fontRenderer, tp.toString(), width / 4, height / 16,
				Integer.parseInt("FFAA00", 16));
		drawCenteredString(minecraft.fontRenderer, ClientDisplayData.getData().tiles.size() + "", width / 4 * 3,
				height / 16, Integer.parseInt("0000FF", 16));
	}

	public static void moveTopBarLeft(float offset)
	{
		topBarOffset += offset;
	}

	public static void moveTopBarRight(float offset)
	{
		topBarOffset -= offset;
		if(topBarOffset < 0) topBarOffset = 0;
	}
}
