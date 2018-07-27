package net.civiscraft.core;

import java.io.File;

import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.registry.CreativeTabManager;
import net.civiscraft.lib.registry.CreativeTabManager.CreativeTabCC;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
	modid = CCCore.MODID,
	name = "CivisCraft Core",
	version = CCLib.VERSION
)

public class CCCore 
{
	public static final String MODID = "civiscraftcore";
	
	@Mod.Instance(MODID)
	public static CCCore INSTANCE = null;
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent e)
	{
		File cfgFolder = e.getModConfigurationDirectory();
		cfgFolder = new File(cfgFolder, "civiscraft");
		CCCoreConfig.preInit(cfgFolder);
		
		CreativeTabCC tab = CreativeTabManager.createTab("civiscraft");
		
		CCCoreItems.preInit();
		CCCoreBlocks.preInit();
		CCCoreStatements.preInit();
		
		CCCoreProxy.getProxy().fmlPreInit();
	}
	
	@Mod.EventHandler
	public static void init(FMLInitializationEvent e)
	{
		CCCoreProxy.getProxy().fmlInit();
		
		CCCoreRecipes.init();
		CCAdvancements.init();
	}
	
	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent e)
	{
		CCCoreProxy.getProxy().fmlPostInit();
		CCCoreConfig.postInit();
	}
}
