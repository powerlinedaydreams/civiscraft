package net.civiscraft.world.biome;

public class PlainsBiomeCC extends BiomeCC
{
	private static String[] biomes = { "plains", "mutated_plains", "savanna", "savanna_rock", "mesa", "mesa_rock",
			"mesa_clear_rock", "mutated_savanna", "mutated_savanna_rock", "mutated_mesa", "mutated_mesa_rock",
			"mutated_mesa_clear_rock" };

	public PlainsBiomeCC()
	{
		super("plains", biomes);
	}
}
