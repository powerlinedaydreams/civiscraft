package net.civiscraft.world.biome;

public class PlainsBiomeCC extends BiomeCC
{
	private PlainsBiomeCC()
	{
		super("plains");
	}

	public static PlainsBiomeCC getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new PlainsBiomeCC();
		}

		return (PlainsBiomeCC) INSTANCE;
	}
}
