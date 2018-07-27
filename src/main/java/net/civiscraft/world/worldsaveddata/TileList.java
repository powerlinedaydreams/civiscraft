package net.civiscraft.world.worldsaveddata;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.net.MessageManager;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.net.cache.CCClientObjectCaches.CacheType;
import net.civiscraft.lib.net.cache.MessageObjectCacheResponse;
import net.civiscraft.lib.util.NBTUtil.NBTType;
import net.civiscraft.world.CCWorldProxy;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.client.tile.ClientTilePos;
import net.civiscraft.world.map.tile.Tile;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.entity.player.EntityPlayerMP;
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
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList xlist = nbt.getTagList("tiles", NBTType.NBTTagCompound.i);
		tiles = new HashMap<TilePos, Tile>();
		
		for(int i=0; i<xlist.tagCount(); i++)
		{
			NBTTagCompound entry = xlist.getCompoundTagAt(i);
			
			Tile tile = Tile.readFromNBT(entry, world);
			tiles.put(tile.getPos(), tile);
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();
		
		for(Map.Entry<TilePos, Tile> entry : tiles.entrySet())
		{
			NBTTagCompound tag = entry.getValue().writeToNBT(new NBTTagCompound());
			list.appendTag(tag);
		}
		
		nbt.setTag("tiles", list);
		return nbt;
	}
	
	public boolean addTile(Tile tile)
	{	 
		return tiles.put(tile.getPos(), tile) != null;
	}
	
	public static TileList get(World world)
	{
		MapStorage storage = world.getMapStorage();
		TileList instance = (TileList)storage.getOrLoadData(TileList.class, DATA_NAME);
		TileList.world = world;
		CCLog.logger.info("Existing TileList loaded");
		
		if (instance == null)
		{
			instance = new TileList();
			storage.setData(DATA_NAME, instance);
			CCLog.logger.info("New TileList created");
		}
		
		return instance;
	}

	public ClientTile getClientTile(TilePos pos, EntityPlayerMP player)
	{
		Tile tile = getTileByPos(pos);
		ClientTile cTile = new ClientTile(tile, player);
		ClientTilePos[] id = {CCWorldProxy.TILE_CACHE.server().store(cTile)};
		
		byte[][] values = new byte[1][];
		PacketBufferCC buffer = new PacketBufferCC(Unpooled.buffer());
		cTile.bufferWrite(buffer);
		values[0] = new byte[buffer.readableBytes()];
		buffer.readBytes(values[0]);
		buffer.clear();
		
		MessageManager.sendTo(new MessageObjectCacheResponse<ClientTilePos>(id, values, CacheType.CTILE), player);
		return cTile;
	}
}
