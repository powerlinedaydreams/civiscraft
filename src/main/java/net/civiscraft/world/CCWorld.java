package net.civiscraft.world;

import net.civiscraft.lib.CCLib;
import net.civiscraft.world.net.TileAddMessage;
import net.civiscraft.world.net.TileAddMessageHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CCWorld.MODID, name = "CivisCraft World", version = CCLib.VERSION)

public class CCWorld
{
	public static final String MODID = "civiscraftworld";
	public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@Instance(MODID)
	public static CCWorld INSTANCE;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent e)
	{
		CCWorldProxy.getProxy().fmlPreInit();

		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, CCWorldProxy.getProxy());
		NETWORK_CHANNEL.registerMessage(TileAddMessageHandler.class, TileAddMessage.class, 0, Side.CLIENT);
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent e)
	{

	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent e)
	{

	}
}
