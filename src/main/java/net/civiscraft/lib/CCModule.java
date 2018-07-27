package net.civiscraft.lib;

import java.util.Locale;

import net.civiscraft.lib.log.CCLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public enum CCModule {
	LIB,
	CORE,
	ECONOMY,
	DIPLOMACY,
	FAITH,
	WORLD;
	
	public static final CCModule[] values = values();
	
	private static final String MODID_START = "civiscraft";
	private final String modid, part;
	
	CCModule()
	{
		part = name().toLowerCase(Locale.ROOT);
		this.modid = MODID_START + part;
	}
	
	public static void fmlPreInit() {}
	
	public static boolean isCCMod(String modid)
	{
		if(!modid.startsWith(MODID_START)) return false;
		
		String post = modid.substring(MODID_START.length());
		
		for(CCModule module : values)
		{
			if(post.equals(module.part)) return true;
		}
		
		return false;
	}
	
	public boolean isLoaded()
	{
		return Loader.isModLoaded(modid);
	}
	
	public String getModId()
	{
		return modid;
	}
	
	static
	{
		if (!Loader.instance().hasReachedState(LoaderState.LOADING))
		{
			throw new RuntimeException("Accessed CC modules too early! They may only be used from construction onwards");
		}
		
		for(CCModule module : values())
		{
			if(module.isLoaded())
			{
				CCLog.logger.info("Module " + module.name().toLowerCase(Locale.ROOT) + " is loaded");
			}
			
			else
			{
				CCLog.logger.info("Module " + module.name().toLowerCase(Locale.ROOT) + " is NOT loaded");
			}
		}
	}
}
