package net.civiscraft.lib.net.cache;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public class CCClientObjectCaches
{
	static final HashMap<CacheType, ClientObjectCache<?,?>> CACHES = new HashMap<>();
	static boolean isClientConnected = false;
	
	public static void registerCache(CacheType type, ClientObjectCache<?, ?> cache)
	{
		if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION))
		{
			throw new IllegalStateException("Unable to register " + type + ": May only construct a cache BEFORE post-init.");
		}
		
		CACHES.put(type, cache);
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
			for(Map.Entry<CacheType, ClientObjectCache<?, ?>> entry : CACHES.entrySet())
			{	
				entry.getValue().onClientWorldTick();
			}
		}
	}
	
	public static void onClientJoinServer()
	{
		isClientConnected = true;
		for(Map.Entry<CacheType, ClientObjectCache<?, ?>> entry : CACHES.entrySet())
		{
			entry.getValue().onClientJoinServer();
		}
	}
	
	public static void onClientLeaveServer()
	{
		isClientConnected = false;
	}
	
	public static ClientObjectCache<?, ?> get(CacheType type)
	{
		return CACHES.get(type);
	}
	
	public enum CacheType
	{
		CTILE((short) 0);
		
		private short enumVar;
		private static HashMap<Short, CacheType> map = new HashMap<>();
		
		static {
			for(CacheType type : CacheType.values())
			{
				map.put(type.enumVar, type);
			}
		}
		
		private CacheType(short var)
		{
			this.enumVar = var;
		}
		
		public short getValue()
		{
			return enumVar;
		}
		
		public static CacheType valueOf(short var)
		{
			return map.get(var);
		}
	}
}
