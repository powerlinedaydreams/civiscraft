package net.civiscraft.core.worldsaveddata;

import java.util.TreeMap;

import net.civiscraft.lib.CCLib;
import net.civiscraft.world.map.tile.Tile;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class WorldTurn extends WorldSavedData
{

	public WorldTurn(String name)
	{
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
