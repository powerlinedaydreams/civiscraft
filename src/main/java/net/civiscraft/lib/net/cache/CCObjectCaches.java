package net.civiscraft.lib.net.cache;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public class CCObjectCaches
{
	static final HashMap<String, NetworkedObjectCache<?>> CACHES = new HashMap<String, NetworkedObjectCache<?>>();
	static boolean isClientConnected = false;
	
	private CCObjectCaches() {}
	
	public static void registerCache(NetworkedObjectCache<?> cache)
	{
		String string = cache.getCacheName();
		
		if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION))
		{
			throw new IllegalStateException("Unable to register " + string + ": May only construct a cache BEFORE post-init.");
		}
		
		CACHES.put(string, cache);
	}
	
	public static void fmlPreInit()
	{
		
	}
	
	public static void fmlPostInit()
	{
	}
	
	public static void onClientTick()
	{
		if(isClientConnected)
		{
			for(Map.Entry<String, NetworkedObjectCache<?>> entry : CACHES.entrySet())
			{
				entry.getValue().onClientWorldTick();
			}
		}
	}
	
	public static void onClientJoinServer()
	{
		isClientConnected = true;
		for(Map.Entry<String, NetworkedObjectCache<?>> entry : CACHES.entrySet())
		{
			entry.getValue().onClientJoinServer();
		}
	}
	
	public static void onClientLeaveServer()
	{
		isClientConnected = false;
	}

	public static NetworkedObjectCache<?> getCache(String string)
	{
		return CACHES.get(string);
	}
}
