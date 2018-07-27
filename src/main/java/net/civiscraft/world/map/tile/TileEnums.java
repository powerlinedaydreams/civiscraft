package net.civiscraft.world.map.tile;

public class TileEnums
{
	public enum Biome
	{
		PLAINS("plains"),
		DESERT("desert");
		
		private String typeName;
		
		private Biome(String s)
		{
			typeName = s;
		}
	}
	
	public enum Terrain
	{
		HILLS("hills"),
		RIVER("river"),
		LAVALAKE("lavalake"),
		RAVINE("ravine"),
		MOUNTAINS("mountains");
		
		private String typeName;
		
		private Terrain(String s)
		{
			typeName = s;
		}
	}
	
	public enum Structure
	{
		FARM("farm"),
		CITY("city");
		
		private String typeName;
		
		private Structure(String s)
		{
			typeName = s;
		}
	}
}
