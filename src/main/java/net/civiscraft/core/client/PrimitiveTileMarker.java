package net.civiscraft.core.client;

import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;

public class PrimitiveTileMarker implements DebugRenderer.IDebugRenderer
{
	private final Minecraft minecraft;

	public PrimitiveTileMarker(Minecraft minecraftIn)
	{
		this.minecraft = minecraftIn;
	}

	@Override
	public void render(float partialTicks, long finishTimeNano)
	{
		EntityPlayer entityplayer = this.minecraft.player;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double) partialTicks;
		double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double) partialTicks;
		double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double) partialTicks;
		double d3 = 0.0D - d1;
		double d4 = 20.0D;
		GlStateManager.disableTexture2D();
		GlStateManager.disableBlend();

		GlStateManager.glLineWidth(1.0F);
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

		TilePos tilePos = new TilePos(new ChunkPos(entityplayer.chunkCoordX, entityplayer.chunkCoordZ));

		for (TilePos tp : tilePos.getTilesInRange(2))
		{
			ChunkPos[][] chunks = tp.generateChunks();

			ChunkPos chunk = chunks[0][0];

			double d5 = (double) (chunk.x << 4) - d0;
			double d6 = (double) (chunk.z << 4) - d2;

			bufferbuilder.pos(d5, d3, d6).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.pos(d5, d3, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
			bufferbuilder.pos(d5, d4, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();

			chunk = chunks[0][3];

			d5 = (double) (chunk.x << 4) - d0;
			d6 = (double) (chunk.z << 4) - d2 + 16;

			bufferbuilder.pos(d5, d4, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
			bufferbuilder.pos(d5, d3, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
			bufferbuilder.pos(d5, d4, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();

			chunk = chunks[3][3];

			d5 = (double) (chunk.x << 4) - d0 + 16;
			d6 = (double) (chunk.z << 4) - d2 + 16;

			bufferbuilder.pos(d5, d4, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
			bufferbuilder.pos(d5, d3, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
			bufferbuilder.pos(d5, d4, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();

			chunk = chunks[3][0];

			d5 = (double) (chunk.x << 4) - d0 + 16;
			d6 = (double) (chunk.z << 4) - d2;

			bufferbuilder.pos(d5, d4, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
			bufferbuilder.pos(d5, d3, d6).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
			bufferbuilder.pos(d5, d3, d6).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
		}

		tessellator.draw();
		GlStateManager.glLineWidth(1.0F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
	}

}
