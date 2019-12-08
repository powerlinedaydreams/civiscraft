package net.civiscraft.world.event;

import net.civiscraft.world.biome.BiomeCC;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BiomeCCRegistryEvent extends Event
{

	@Cancelable
	public static class Registry extends BiomeCCRegistryEvent
	{
		private final BiomeCC biome;

		public Registry(BiomeCC biome)
		{
			this.biome = biome;
		}

		public BiomeCC getBiome()
		{
			return biome;
		}
	}

	public static class Completed extends BiomeCCRegistryEvent
	{
		public Completed()
		{
		}
	}
}
