package net.civiscraft.world.biome;

import java.util.HashMap;
import java.util.Map;

import net.civiscraft.lib.log.CCLog;
import net.civiscraft.world.event.BiomeCCRegistryEvent;

public abstract class BiomeCC
{
	public static Map<String, BiomeCC> BIOMES = new HashMap<String, BiomeCC>();

	public final String name;
	public String[] minecraftBiomes;

	public BiomeCC(String name, String[] biomes)
	{
		this.name = name;
		this.minecraftBiomes = biomes;
	}

	public static void register(BiomeCC biome)
	{
		Boolean register = net.minecraftforge.common.MinecraftForge.EVENT_BUS
				.post(new BiomeCCRegistryEvent.Registry(biome));
		if(!register || biome.name.equals("plains"))
		{
			BIOMES.put(biome.name, biome);
			CCLog.logger.info(biome.name);
		}
	}
}
