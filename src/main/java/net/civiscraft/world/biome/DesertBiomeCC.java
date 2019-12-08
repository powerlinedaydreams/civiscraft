package net.civiscraft.world.biome;

public class DesertBiomeCC extends BiomeCC
{
	private static String[] biomes = { "desert", "savanna", "savannah_rock", "mesa", "mesa_rock", "mesa_clear_rock",
			"mutated_desert", "mutated_savanna", "mutated_savanna_rock", "mutated_mesa", "mutated_mesa_rock",
			"mutated_mesa_clear_rock" };

	public DesertBiomeCC()
	{
		super("desert", biomes);
	}
}
