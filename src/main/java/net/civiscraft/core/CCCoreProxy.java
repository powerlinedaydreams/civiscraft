package net.civiscraft.core;

import net.civiscraft.core.client.RenderTickListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CCCoreProxy implements IGuiHandler
{
	@SidedProxy(modId = CCCore.MODID)
	private static CCCoreProxy proxy = null;
	
	public static CCCoreProxy getProxy ()
	{
		return proxy;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		/*if(ID == CCCoreGui.LIST.ordinal())
		{
			return new ContainerList(player);
		}*/
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
	
	//Intentionally left blank
	public void fmlPreInit() {}
	public void fmlInit() {}
	public void fmlPostInit() {}
	
	@SideOnly(Side.SERVER)
	public static class ServerProxy extends CCCoreProxy
	{
		@Override
		public void fmlPreInit()
		{
			
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class ClientProxy extends CCCoreProxy
	{
		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
		{
			/*if(ID == CCCoreGui.LIST.ordinal())
			{
				return new GuiList(player);
			}*/
			
			return null;
		}
		
		@Override
		public void fmlPreInit()
		{
			super.fmlPreInit();
			CCCoreModels.preInit();
		}
		
		@Override
		public void fmlInit()
		{
			CCCoreModels.init();
			MinecraftForge.EVENT_BUS.register(RenderTickListener.class);
		}
	}
}
