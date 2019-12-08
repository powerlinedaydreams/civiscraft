package net.civiscraft.world.biome;

public class ForestBiomeCC extends BiomeCC
{
	private static String[] biomes = { "Forest", "Taiga", "ForestHills", "TaigaHills", "BirchForest",
			"BirchForestHills", "RoofedForest", "TaigaCold", "taiga_cold_hills", "redwood_taiga", "redwood_taiga_hills",
			"savanna", "savanna_rock", "mutated_forest", "mutated_taiga", "mutated_birch_forest",
			"mutated_birch_forest_hills", "mutated_roofed_forest", "mutated_taiga_cold", "mutated_redwood_taiga",
			"mutated_redwood_taiga_hills", "mutated_savanna", "mutated_savanna_rock" };

	public ForestBiomeCC()
	{
		super("forest", biomes);
	}
}
