package net.civiscraft.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class CCCoreConfig {
	public static Configuration config;
	
	public static void preInit(File cfgFolder)
	{
		config = new Configuration(new File(cfgFolder, "main.cfg"));
	}
	
	public static void postInit()
	{
		//localization changes
	}
}
