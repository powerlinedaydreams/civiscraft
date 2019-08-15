package net.civiscraft.world.biome;

import java.util.HashMap;
import java.util.Map;

public abstract class BiomeCC
{
	public static Map<String, BiomeCC> BIOMES = new HashMap<String, BiomeCC>();

	public static BiomeCC INSTANCE;
	public final String name;

	public BiomeCC(String name)
	{
		this.name = name;
		BIOMES.put(name, this);
	}
}
