package net.civiscraft.world;

import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.log.CCLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(
	modid = CCWorld.MODID,
	name = "CivisCraft World",
	version = CCLib.VERSION
)

public class CCWorld
{
	public static final String MODID = "civiscraftworld";
	
	@Instance(MODID)
	public static CCWorld INSTANCE;
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent e)
	{
		CCWorldProxy.getProxy().fmlPreInit();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, CCWorldProxy.getProxy());
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
