package net.civiscraft.world;

import net.civiscraft.lib.client.render.chrome.ChromeRenderer;
import net.civiscraft.lib.net.cache.CCClientObjectCaches;
import net.civiscraft.lib.net.cache.CCClientObjectCaches.CacheType;
import net.civiscraft.world.client.tile.render.chrome.TileChromeRenderer;
import net.civiscraft.world.event.WorldEventHandler;
import net.civiscraft.world.net.ClientTileCache;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CCWorldProxy implements IGuiHandler
{
	public static final ClientTileCache TILE_CACHE = new ClientTileCache();
	public static TileChromeRenderer CT_RENDERER;
	
	@SidedProxy(modId = CCWorld.MODID)
	private static CCWorldProxy proxy;
	
	public static CCWorldProxy getProxy()
	{
		return proxy;
	}
	
	void fmlPreInit()
	{
		MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
		
		CCClientObjectCaches.registerCache(CacheType.CTILE, TILE_CACHE);
	}
	
	void fmlInit() {}
	
	void fmlPostInit() {}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
	
	@SideOnly(Side.SERVER)
	public static class ServerProxy extends CCWorldProxy
	{
		@Override
		public void fmlPreInit()
		{
			super.fmlPreInit();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class ClientProxy extends CCWorldProxy
	{
		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
		{
			return null;
		}
		
		@Override
		public void fmlPreInit()
		{
			super.fmlPreInit();
			CT_RENDERER = new TileChromeRenderer(Minecraft.getMinecraft());
			ChromeRenderer.register(CT_RENDERER);
		}
		
		@Override
		public void fmlInit()
		{
			
		}
	}
}
