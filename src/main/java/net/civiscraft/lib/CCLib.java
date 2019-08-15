package net.civiscraft.lib;

import net.civiscraft.lib.chunkload.ChunkLoader;
import net.civiscraft.lib.log.CCLog;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(
	modid = CCLib.MODID,
	name = "CivisCraft Lib",
	version = CCLib.VERSION
)

public class CCLib 
{
	public static final String MODID = "civiscraftlib";
	public static final String VERSION = "${version}";
	public static final String MC_VERSION = "${mcversion}";
	
	@Instance(MODID)
	public static CCLib INSTANCE;
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent e)
	{
		CCLog.logger.info("");
		CCLog.logger.info("Starting CivisCraft " + CCLib.VERSION);
		CCLog.logger.info("Copyright (c) the CivisCraft team, 2017");
		CCLog.logger.info("https://civiscraft.net/info");
		CCLog.logger.info("");
		
		CCModule.fmlPreInit();
		CCLibProxy.getProxy().fmlPreInit();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, CCLibProxy.getProxy());
	}
	
	@Mod.EventHandler
	public static void init(FMLInitializationEvent e)
	{
		
	}
	
	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent e)
	{
		ForgeChunkManager.setForcedChunkLoadingCallback(INSTANCE, new ChunkLoader());
	}
}
