package net.civiscraft.core;

import java.io.File;

import net.civiscraft.core.net.EmpireRequestMessage;
import net.civiscraft.core.net.EmpireRequestMessageHandler;
import net.civiscraft.core.net.EmpireUpdateMessage;
import net.civiscraft.core.net.EmpireUpdateMessageHandler;
import net.civiscraft.core.net.PlayerEmpireAddMessage;
import net.civiscraft.core.net.PlayerEmpireAddMessageHandler;
import net.civiscraft.core.net.TileClaimMessage;
import net.civiscraft.core.net.TileClaimMessageHandler;
import net.civiscraft.core.proxy.CCCoreProxy;
import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.registry.CreativeTabManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CCCore.MODID, name = "CivisCraft Core", version = CCLib.VERSION, guiFactory = "net.civiscraft.core.CCCoreGui")

public class CCCore
{
	public static final String MODID = "civiscraftcore";
	public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@Mod.Instance(MODID)
	public static CCCore INSTANCE = null;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent e)
	{
		File cfgFolder = e.getModConfigurationDirectory();
		cfgFolder = new File(cfgFolder, "civiscraft");
		CCCoreConfig.preInit(cfgFolder);

		CreativeTabManager.createTab("civiscraft");

		CCCoreItems.preInit();
		CCCoreBlocks.preInit();
		CCCoreStatements.preInit();

		CCCoreProxy.getProxy().preInit();

		NETWORK_CHANNEL.registerMessage(EmpireRequestMessageHandler.class, EmpireRequestMessage.class, 0, Side.SERVER);
		NETWORK_CHANNEL.registerMessage(EmpireUpdateMessageHandler.class, EmpireUpdateMessage.class, 1, Side.CLIENT);
		NETWORK_CHANNEL.registerMessage(PlayerEmpireAddMessageHandler.class, PlayerEmpireAddMessage.class, 2,
				Side.CLIENT);
		NETWORK_CHANNEL.registerMessage(TileClaimMessageHandler.class, TileClaimMessage.class, 3, Side.SERVER);
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent e)
	{
		CCCoreProxy.getProxy().init();

		CCCoreRecipes.init();
		CCAdvancements.init();
	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent e)
	{
		CCCoreProxy.getProxy().postInit();
		CCCoreConfig.postInit();
	}
}
