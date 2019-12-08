package net.civiscraft.world.client.tile;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.core.tile.Yield;
import net.civiscraft.lib.cap.intel.IIntel;
import net.civiscraft.lib.client.CCClientObject;
import net.civiscraft.world.biome.BiomeCC;
import net.civiscraft.world.map.tile.Tile;
import net.civiscraft.world.map.tile.TileEnums.Structure;
import net.civiscraft.world.map.tile.TileEnums.Terrain;
import net.civiscraft.world.map.tile.TileOwner;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.resource.Resource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;

public class ClientTile extends CCClientObject
{
	//Not subject to player intel
	public final TilePos pos;
	private Tile tile;
	private EntityPlayer player;
	public UUID playerId;
	public ChunkPos[][] chunks;
	public BiomeCC biome;
	//public ArrayList<Terrain> terrain;
	//public ArrayList<Structure> structures;

	//Subject to player intel
	/**
	 * Will be UNKNOWN if the player hasn't met the owner Empire
	 */
	public TileOwner owner;

	/**
	 * A TreeMap of Resources and Integers, representing the Resources on the
	 * referenced Tile and the amount of each on it. It will not contain Resources
	 * the player has not yet discovered
	 */
	public TreeMap<Resource, Integer> resources;

	/**
	 * The Yield of the referenced Tile, based on the edited Resource values
	 */
	public TreeMap<Yield, Integer> yield;

	/**
	 * 
	 * @param tile
	 *            The Tile being referenced
	 * @param player
	 *            The EntityPlayer context the ClientTile is based on (i.e. the
	 *            EntityPlayer requesting the ClientTile)
	 */

	public ClientTile(Tile tile, EntityPlayer player)
	{
		this.tile = tile;
		this.player = player;
		this.playerId = player.getUniqueID();
		this.pos = tile.getPos();
		this.chunks = generateChunks();
		this.biome = tile.getBiome();
		//this.terrain = tile.getTerrain();
		//this.structures = tile.getStructures();
		this.owner = generateOwner(tile);
		this.resources = generateResources();
		this.yield = generateYield();
		this.tile = null;
		this.player = null;

		//CCWorldProxy.TILE_CACHE.server().store(this);
	}

	private TreeMap<Yield, Integer> generateYield()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString()
	{
		return pos + ": " + playerId;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean result;

		if(obj == null || pos == null || getClass() != obj.getClass())
		{
			result = false;
		}

		else
		{
			ClientTile tile = (ClientTile) obj;
			result = pos == tile.pos && playerId == tile.playerId;
		}

		return result;
	}

	public ClientTile()
	{
		this.pos = null;
	}

	public ClientTile(ClientTile obj)
	{
		this.player = obj.player;
		this.playerId = obj.playerId;
		this.pos = obj.pos;
		this.chunks = obj.chunks;
		this.biome = obj.biome;
		//this.terrain = obj.terrain;
		//this.structures = obj.structures;
		this.owner = obj.owner;
		this.resources = obj.resources;
	}

	public ClientTile(TilePos _pos, UUID _playerId, BiomeCC _biome, ArrayList<Terrain> _terrain,
			ArrayList<Structure> _structures, TreeMap<Resource, Integer> _resources, TileOwner _owner)
	{
		this.pos = _pos;
		this.playerId = _playerId;
		this.biome = _biome;
		//this.terrain = _terrain;
		//this.structures = _structures;
		this.resources = _resources;
		this.owner = _owner;
		this.chunks = generateChunks();
		this.yield = generateYield();
	}

	public ClientTile(TilePos pos)
	{
		this.pos = pos;
	}

	private TreeMap<Resource, Integer> generateResources()
	{
		TreeMap<Resource, Integer> allResources = tile.getResources();
		TreeMap<Resource, Integer> knownResources = new TreeMap<Resource, Integer>();
		IIntel playerIntel = player.getCapability(CapPlayerIntel.CAP, null);
		ArrayList<Resource> playerResources = playerIntel.getResources();
		for (Resource resource : playerResources)
		{
			Integer value = allResources.get(resource);
			if(value != null)
			{
				knownResources.put(resource, value);
			}
		}

		return knownResources;
	}

	private TileOwner generateOwner(Tile tile)
	{
		TileOwner owner = tile.getOwner();
		if(owner.isOwned)
		{
			UUID ownerId = owner.getOwner();
			IIntel playerIntel = player.getCapability(CapPlayerIntel.CAP, null);
			ArrayList<UUID> playerEmpires = playerIntel.getEmpires();

			if(!playerEmpires.contains(ownerId))
			{
				return TileOwner.unknown(pos);
			}
		}

		return owner;
	}

	private ChunkPos[][] generateChunks()
	{
		ChunkPos[][] chunkArray = new ChunkPos[4][4];

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				int z = pos.z * 4 + j;
				int x = pos.x * 4 - 2 * (pos.z % 2);

				chunkArray[i][j] = new ChunkPos(x, z);
			}
		}

		return chunkArray;
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeLong(playerId.getMostSignificantBits());
		buf.writeLong(playerId.getLeastSignificantBits());
		buf.writeShort(biome.name.length());
		buf.writeCharSequence(biome.name, Charset.forName("UTF-8"));

		/*buf.writeShort(terrain.size());
		for (Terrain ter : terrain)
		{
			buf.writeShort(ter.typeName.length());
			buf.writeCharSequence(ter.typeName, Charset.forName("UTF-8"));
		}
		
		buf.writeShort(structures.size());
		for (Structure str : structures)
		{
			buf.writeShort(str.typeName.length());
			buf.writeCharSequence(str.typeName, Charset.forName("UTF-8"));
		}*/

		buf.writeShort(resources.size());
		for (Map.Entry<Resource, Integer> entry : resources.entrySet())
		{
			buf.writeShort(entry.getKey().name.length());
			buf.writeCharSequence(entry.getKey().name, Charset.forName("UTF-8"));
			buf.writeInt(entry.getValue());
		}

		boolean ownerUnknown = Empire.NULL.equals(owner.getOwner());
		buf.writeBoolean(ownerUnknown);

		if(!ownerUnknown)
		{
			boolean isOwned = owner.isOwned;
			buf.writeBoolean(isOwned);

			if(isOwned)
			{
				buf.writeLong(owner.getOwner().getMostSignificantBits());
				buf.writeLong(owner.getOwner().getLeastSignificantBits());
			}
		}
	}

	public static ClientTile fromBytes(ByteBuf buf)
	{
		TilePos _pos = TilePos.fromLong(buf.readLong());
		long l1 = buf.readLong();
		long l2 = buf.readLong();
		UUID _playerId = new UUID(l1, l2);
		int length = buf.readShort();
		String str = buf.readCharSequence(length, Charset.forName("UTF-8")).toString();

		BiomeCC _biome = BiomeCC.BIOMES.get(str);

		/*int size = buf.readShort();
		ArrayList<Terrain> _terrain = new ArrayList<Terrain>(size);
		for (int i = 0; i < size; i++)
		{
			length = buf.readShort();
			_terrain.add(Terrain.valueOf(buf.readCharSequence(length, Charset.forName("UTF-8")).toString()));
		}
		
		size = buf.readShort();
		ArrayList<Structure> _structures = new ArrayList<Structure>(size);
		for (int i = 0; i < size; i++)
		{
			length = buf.readShort();
			_structures.add(Structure.valueOf(buf.readCharSequence(length, Charset.forName("UTF-8")).toString()));
		}*/

		int size = buf.readShort();
		TreeMap<Resource, Integer> _resources = new TreeMap<Resource, Integer>();
		for (int i = 0; i < size; i++)
		{
			length = buf.readShort();
			Resource res = Resource.RESOURCES.get(buf.readCharSequence(length, Charset.forName("UTF-8")).toString());
			int value = buf.readInt();
			_resources.put(res, value);
		}

		TileOwner _owner;
		boolean ownerUnknown = buf.readBoolean();

		if(ownerUnknown)
		{
			_owner = TileOwner.unknown(_pos);
		}

		else
		{
			boolean isOwned = buf.readBoolean();
			UUID ownerId;

			if(isOwned)
			{
				ownerId = new UUID(buf.readLong(), buf.readLong());
				_owner = new TileOwner(_pos, isOwned, ownerId);
			}

			else
			{
				_owner = new TileOwner(_pos, isOwned, null);
			}
		}

		return new ClientTile(_pos, _playerId, _biome, null, null, _resources, _owner);
	}

	public ClientTilePos getClientTilePos()
	{
		return new ClientTilePos(pos, playerId);
	}
}
