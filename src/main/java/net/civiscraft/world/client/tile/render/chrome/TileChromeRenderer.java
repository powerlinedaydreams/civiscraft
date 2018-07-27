package net.civiscraft.world.client.tile.render.chrome;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.opengl.GL11;

import net.civiscraft.lib.client.render.chrome.ChromeRenderer;
import net.civiscraft.lib.client.render.chrome.ChromeRenderer.IChromeRenderer;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.net.cache.ClientObjectCache;
import net.civiscraft.world.CCWorldProxy;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.client.tile.ClientTilePos;
import net.civiscraft.world.map.tile.TileBorder;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.net.ClientTileCache;
import net.civiscraft.world.net.ClientTileCache.CTileLink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileChromeRenderer implements IChromeRenderer
{
	private final Minecraft minecraft;
	private EntityPlayer player;
	private TilePos currentTile;
	private final ClientTileCache cache = CCWorldProxy.TILE_CACHE;
	static float alpha = 0.8F;
	static float[] defaultTileColor = {0.5f, 0.5f, 0.5f};
	static float[] unseenTileColor = {0.1f, 0.1f, 0.1f};
	Tessellator tessellator;
	BufferBuilder bufferBuilder;
	double d0, d1, d2;
	ClientObjectCache<ClientTile, ClientTilePos>.Link[] renderTiles;
	TilePos[] renderPosArray;
	
	public TileChromeRenderer(Minecraft minecraftIn)
	{
		this.minecraft = minecraftIn;
		this.player = minecraftIn.player;
		this.tessellator = Tessellator.getInstance();
		this.bufferBuilder = tessellator.getBuffer();
		ChromeRenderer.register(this);
	}
	
	public void render(float partialTicks)
	{
		if(player == null)
		{
			player = minecraft.player;
			
			if(player == null)
			{
				return;
			}
		}
		
		TilePos previous = currentTile;
		currentTile = new TilePos(new ChunkPos(player.chunkCoordX, player.chunkCoordZ));
		
		if(previous != currentTile)
		{
			renderTiles = cache.getTilesInRange(new ClientTilePos(currentTile, player), 2);
			renderPosArray = currentTile.getTilesInRange(2);
		}
		
		d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
		d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
		d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
		
		GlStateManager.disableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.glLineWidth(1.0F);
		GlStateManager.enableCull();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		for(int i = 0; i < renderTiles.length; i++)
		{
			CTileLink link = (CTileLink) renderTiles[i];
			
			if(link == null || link.get() == null || link.get().pos == null)
			{
				CCLog.logger.info("Link or tile was null");  ///////////////////////////////////////////////
				renderTiles[i] = cache.client().retrieve(new ClientTilePos(renderPosArray[i], player));
				continue;
			}
			
			ClientTile tile = link.get();
			
			CCLog.logger.info("Tile was not null"); //////////////////////////////////////////////////////
			
			TileBorder border = tile.tileBorder;
			
			if(border == null || !border.complete)
			{
				ChunkPos indexChunk = tile.pos.getIndexChunkPos();
				float[] color = tile.owner.UNKNOWN ? unseenTileColor : tile.owner.isOwned ? defaultTileColor : defaultTileColor;
				double x = (double) (indexChunk.x << 4);
	        		double y = player.posY + 1.0d;
	        		double z = (double) (indexChunk.z << 4);
	        		
	        		bufferBuilder.pos(x - 0.01 - d0, 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0 , 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0 , y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		
	        		bufferBuilder.pos(x - 0.01 - d0, 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		
	        		bufferBuilder.pos(x + 63.99 - d0, 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		
	        		bufferBuilder.pos(x + 0.01 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 0.01 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		
	        		bufferBuilder.pos(x - 0.01 - d0, 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0 , y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0 , 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		
	        		bufferBuilder.pos(x - 0.01 - d0, 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x - 0.01 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		
	        		bufferBuilder.pos(x + 63.99 - d0, 0.0d - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, y - d1, z - 0.01 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		
	        		bufferBuilder.pos(x + 0.01 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, 0.0d - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 63.99 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
	        		bufferBuilder.pos(x + 0.01 - d0, y - d1, z + 63.99 - d2).color(color[0], color[1], color[2], alpha).endVertex();
			}
			
			else
			{
				renderBorder(tile);
			}
		}
		
		tessellator.draw();
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
	}
	
	private void renderBorder(ClientTile tile)
	{
		TileBorder border = tile.tileBorder;
		float[] color = tile.owner.UNKNOWN ? unseenTileColor : tile.owner.isOwned ? defaultTileColor : defaultTileColor;
    	
	    	ArrayList<TreeMap<BlockPos, Integer>> map = border.northBorder;
	    	
	    	//NorthBorder (lowest x, lowest z): increase x, z constant
	    	for(int i = 0; i < map.size(); i++)
	    	{
	    		TreeMap<BlockPos, Integer> thisMap = map.get(i);
	    		
	    		for(Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
	    		{
	    			BlockPos pos = entry.getKey();
	    			double x1 = (double) (pos.getX() - .01d - d0);
	    			double x2 = (double) (x1 + entry.getValue() + 1.02);
	    			double y1 = (double) (pos.getY() - .01d - d1);
	    			double y2 = (double) (y1 + 1.02d);
	    			double z1 = (double) (pos.getZ() - .01d - d2);
	    			double z2 = (double) (z1 + 0.3d);
	    			
	    			//-x (north) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+x (south) face
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-y (bottom) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+y (top) face
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-z (west) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+z (east) face
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    		}
	    	}
	    	
	    	map = border.eastBorder;
	    	
	    	//EastBorder (highest x, lowest z): increase z, x constant
	    	for(int i = 0; i < map.size(); i++)
	    	{
	    		TreeMap<BlockPos, Integer> thisMap = map.get(i);
	    		
	    		for(Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
	    		{
	    			BlockPos pos = entry.getKey();
	    			double x2 = (double) (pos.getX() + 1.01d - d0);
	    			double x1 = (double) (x2 - 0.3d);
	    			double y1 = (double) (pos.getY() - 0.01d - d1);
	    			double y2 = (double) (y1 + 1.02d);
	    			double z1 = (double) (pos.getZ() - 0.01d - d2);
	    			double z2 = (double) (z1 + entry.getValue() + 1.02d);
	    			
	    			//-x (north) face
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+x (south) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-y (bottom) face
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+y (top) face
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-z (west) face
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+z (east) face
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    		}
	    	}
	    	    	
			map = border.southBorder;
			
	    	//SouthBorder (highest x, highest z): decrease x, z constant
	    	for(int i = 0; i < map.size(); i++)
	    	{
	    		TreeMap<BlockPos, Integer> thisMap = map.get(i);
	    		
	    		for(Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
	    		{
	    			BlockPos pos = entry.getKey();
	    			double x2 = (double) (pos.getX() + 1.01d - d0);
	    			double x1 = (double) (x2 - entry.getValue() - 1.02d);
	    			double y1 = (double) (pos.getY() - .01d - d1);
	    			double y2 = (double) (y1 + 1.02d);
	    			double z2 = (double) (pos.getZ() + 1.01d - d2);
	    			double z1 = (double) (z2 - 0.3d);
	    			
	    			//-x (north) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+x (south) face
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-y (bottom) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+y (top) face
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-z (west) face
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+z (east) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    		}
	    	}
	    	
			map = border.westBorder;
	    	//WestBorder (lowest x, highest z): decrease z, x constant
	    	for(int i = 0; i < map.size(); i++)
	    	{
	    		TreeMap<BlockPos, Integer> thisMap = map.get(i);
	    		
	    		for(Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
	    		{
	    			BlockPos pos = entry.getKey();
	    			double x1 = (double) (pos.getX() - .01d - d0);
	    			double x2 = (double) (x1 + 0.3d);
	    			double y1 = (double) (pos.getY() - .01d - d1);
	    			double y2 = (double) (y1 + 1.02d);
	    			double z2 = (double) (pos.getZ() + 1.01d - d2);
	    			double z1 = (double) (z2 - entry.getValue() - 1.02d);
	    			
	    			//-x (north) face
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+x (south) face
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-y (bottom) face
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+y (top) face
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//-z (west) face
	    			bufferBuilder.pos(x1, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z2).color(color[0], color[1], color[2], alpha).endVertex();
	    			
	    			//+z (east) face
	    			bufferBuilder.pos(x1, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x1, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y2, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    			bufferBuilder.pos(x2, y1, z1).color(color[0], color[1], color[2], alpha).endVertex();
	    		}
	    	}
	}
}
