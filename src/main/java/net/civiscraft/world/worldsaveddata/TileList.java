package net.civiscraft.world.worldsaveddata;

import java.util.HashMap;
import java.util.Map;

import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.util.NBTUtil.NBTType;
import net.civiscraft.world.map.tile.Tile;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class TileList extends WorldSavedData
{
	private static final String DATA_NAME = CCLib.MODID + "_TileList";

	private HashMap<TilePos, Tile> tiles = new HashMap<TilePos, Tile>();
	private static World world;

	public TileList()
	{
		super(DATA_NAME);
	}

	public TileList(String s)
	{
		super(s);
	}

	public Tile getTileByPos(TilePos pos)
	{
		return tiles.get(pos);
	}

	public int getSize()
	{
		return tiles.size();
	}

	public void tileUpdated()
	{
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList xlist = nbt.getTagList("tiles", NBTType.NBTTagCompound.i);
		CCLog.logger.info("Tags:" + xlist.tagCount());

		for (int i = 0; i < xlist.tagCount(); i++)
		{
			NBTTagCompound entry = xlist.getCompoundTagAt(i);

			Tile tile = Tile.readFromNBT(entry, world);
			tiles.put(tile.getPos(), tile);
		}

		CCLog.logger.info(tiles.size());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		CCLog.logger.info("TileList saved!");
		NBTTagList list = new NBTTagList();

		for (Map.Entry<TilePos, Tile> entry : tiles.entrySet())
		{
			NBTTagCompound tag = entry.getValue().writeToNBT(new NBTTagCompound());
			list.appendTag(tag);
		}

		nbt.setTag("tiles", list);
		return nbt;
	}

	public void addTile(Tile tile)
	{
		tiles.put(tile.getPos(), tile);
		this.markDirty();
	}

	public static TileList get(World world)
	{
		MapStorage storage = world.getMapStorage();
		TileList instance = (TileList) storage.getOrLoadData(TileList.class, DATA_NAME);
		TileList.world = world;

		if(instance == null)
		{
			instance = new TileList();
			storage.setData(DATA_NAME, instance);
		}

		return instance;
	}
}
