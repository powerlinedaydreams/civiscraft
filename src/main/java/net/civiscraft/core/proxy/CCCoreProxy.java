package net.civiscraft.core.proxy;

import net.civiscraft.core.CCCore;
import net.civiscraft.lib.log.CCLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CCCoreProxy implements IGuiHandler
{
	@SidedProxy(modId = CCCore.MODID, clientSide = "net.civiscraft.core.proxy.ClientProxy", serverSide = "net.civiscraft.core.proxy.ServerProxy")
	private static CCCoreProxy proxy;

	public static CCCoreProxy getProxy()
	{
		CCLog.logger.info("get Proxy");
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
	public void preInit()
	{
	}

	public void init()
	{
	}

	public void postInit()
	{
	}
}
