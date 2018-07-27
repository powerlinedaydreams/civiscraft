package net.civiscraft.lib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.civiscraft.lib.client.render.chrome.ChromeRenderer;
import net.civiscraft.lib.event.LibEventHandler;
import net.civiscraft.lib.net.MessageManager;
import net.civiscraft.lib.net.MessageManager.MessageID;
import net.civiscraft.lib.net.cache.MessageObjectCacheRequest;
import net.civiscraft.lib.net.cache.MessageObjectCacheResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CCLibProxy implements IGuiHandler
{
	@SidedProxy(modId = CCLib.MODID)
	private static CCLibProxy proxy;
	
	public static ChromeRenderer CHROME_RENDERER;
	
	public static CCLibProxy getProxy()
	{
		return proxy;
	}
	
	void fmlPreInit()
	{
		MinecraftForge.EVENT_BUS.register(new LibEventHandler());
		MessageManager.addType(MessageID.CCLIB_CACHE_REQUEST, MessageObjectCacheRequest.class, MessageObjectCacheRequest.HANDLER, Side.SERVER);
		MessageManager.addType(MessageID.CCLIB_CACHE_RESPONSE, MessageObjectCacheResponse.class, MessageObjectCacheResponse.HANDLER, Side.CLIENT);
		CCCaps.registerCaps();
	}
	
	void fmlInit() {}
	
	void fmlPostInit() {}
	
	public World getClientWorld()
	{
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return null;
	}
	
	public EntityPlayer getPlayerForContext(MessageContext ctx)
	{
		return ctx.getServerHandler().player;
	}
	
	public void addScheduledTask(World world, Runnable task)
	{
		if(world instanceof WorldServer)
		{
			WorldServer server = (WorldServer) world;
			server.addScheduledTask(task);
		}
	}
	
	public <T extends TileEntity> T getServerTile(T tile)
	{
		return tile;
	}
	
	public InputStream getStreamForIdentifier(ResourceLocation identifier) throws IOException
	{
		return null;
	}
	
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
	
	@SideOnly(Side.CLIENT)
	public static class ClientProxy extends CCLibProxy
	{
		@Override
		void fmlPreInit()
		{
			super.fmlPreInit();
			CHROME_RENDERER = new ChromeRenderer(Minecraft.getMinecraft());
		}
		
		@Override
		void fmlInit()
		{
			super.fmlInit();
		}
		
		@Override
		void fmlPostInit()
		{
			super.fmlPostInit();
		}
		
		@Override
		public World getClientWorld()
		{
			return Minecraft.getMinecraft().world;
		}
		
		@Override
		public EntityPlayer getClientPlayer()
		{
			return Minecraft.getMinecraft().player;
		}
		
		@Override
		public EntityPlayer getPlayerForContext(MessageContext ctx)
		{
			if(ctx.side == Side.SERVER) {return super.getPlayerForContext(ctx);}
			
			return getClientPlayer();
		}
		
		@Override
		public void addScheduledTask(World world, Runnable task)
		{
			if(world instanceof WorldClient)
			{
				Minecraft.getMinecraft().addScheduledTask(task);
			}
			
			else
			{
				super.addScheduledTask(world, task);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <T extends TileEntity> T getServerTile(T tile)
		{
			if(tile != null && tile.hasWorld())
			{
				World world = tile.getWorld();
				
				if(world.isRemote && Minecraft.getMinecraft().isSingleplayer())
				{
					WorldServer server = DimensionManager.getWorld(world.provider.getDimension());
					
					if(server == null) {return tile;}
					
					TileEntity atServer = server.getTileEntity(tile.getPos());
					
					if(atServer == null) {return tile;}
					
					if(atServer.getClass() == tile.getClass()) {return (T) atServer;}
				}
			}
			
			return tile;
		}
		
		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
		{
			return null;
		}
		
		@Override
		public InputStream getStreamForIdentifier(ResourceLocation identifier) throws IOException
		{
			IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(identifier);
			
			if(resource == null)
			{
				throw new FileNotFoundException(identifier.toString());
			}
			
			return resource.getInputStream();
		}
	}
	
	@SideOnly(Side.SERVER)
	public static class ServerProxy extends CCLibProxy
	{
		@Override
		void fmlPreInit()
		{
			super.fmlPreInit();
		}
	}
}
