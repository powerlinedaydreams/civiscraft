package net.civiscraft.world.map.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.civiscraft.core.tile.Yield;
import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.util.NBTUtil;
import net.civiscraft.world.biome.BiomeCC;
import net.civiscraft.world.map.tile.TileEnums.Structure;
import net.civiscraft.world.map.tile.TileEnums.Terrain;
import net.civiscraft.world.resource.Resource;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Tile
{
	private final TilePos pos;
	private final World world;
	private final ChunkPos[][] chunks;
	private TileOwner owner;
	private BiomeCC biome = null;
	private ArrayList<TileEnums.Terrain> terrain;
	private ArrayList<TileEnums.Structure> structures;
	private TreeMap<Resource, Integer> resources;
	private TreeMap<Yield, Integer> yield;
	private TileBorder tileBorder;
	private Ticket chunkTicket = null;
	private boolean forced = false;
	private HashSet<EntityPlayerMP> watchers = new HashSet<EntityPlayerMP>();

	public Tile(TilePos pos, World world)
	{
		this.pos = pos;
		this.world = world;
		this.chunkTicket = ForgeChunkManager.requestTicket(CCLib.INSTANCE, world, ForgeChunkManager.Type.NORMAL);
		this.chunks = generateChunks();
		this.terrain = generateTerrain();
		this.biome = generateBiome();
		this.structures = generateStructures();
		this.yield = generateYield();
		this.owner = new TileOwner(this);
		this.tileBorder = new TileBorder(pos, world, this);

		TileList tileList = TileList.get(world);
		tileList.addTile(this);
		tileBorder.generate();
	}

	private Tile(TilePos pos, World world, TileOwner owner, BiomeCC biome, ArrayList<TileEnums.Terrain> terrain,
			ArrayList<TileEnums.Structure> structures, TileBorder tileBorder)
	{
		this.pos = pos;
		this.world = world;
		//this.chunkTicket = ForgeChunkManager.requestTicket(CCLib.INSTANCE, world, ForgeChunkManager.Type.NORMAL);
		this.owner = owner;
		this.biome = biome;
		this.terrain = terrain;
		this.structures = structures;
		this.tileBorder = tileBorder;
		this.chunks = generateChunks();
		this.yield = generateYield();
		CCLog.logger.info(pos + " loaded from NBT"); //////////////////////////////////////////////////////////////
	}

	private ChunkPos[][] generateChunks()
	{
		return pos.generateChunks();
	}

	public void force()
	{
		/*
		forced = true;
		
		if(chunkTicket == null)
		{
			chunkTicket = ForgeChunkManager.requestTicket(CCLib.INSTANCE, world, ForgeChunkManager.Type.NORMAL);
		}
		
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				ForgeChunkManager.forceChunk(chunkTicket, chunks[i][j]);
			}
		}*/
	}

	public void unforce()
	{
		forced = false;

		if(chunkTicket == null)
		{
			return;
		}

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				ForgeChunkManager.unforceChunk(chunkTicket, chunks[i][j]);
			}
		}
	}

	private BiomeCC generateBiome()
	{
		Map<BiomeCC, Integer> biomeTracker = new HashMap<BiomeCC, Integer>(BiomeCC.BIOMES.size());
		BiomeCC plains = BiomeCC.BIOMES.get("plains");

		for (BiomeCC biome : BiomeCC.BIOMES.values())
		{
			biomeTracker.put(biome, 0);
		}

		biomeTracker.replace(plains, 1);

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				ChunkPos chunk = chunks[i][j];

				for (int x = 0; x < 16; x++)
				{
					for (int z = 0; z < 16; z++)
					{
						BlockPos blockPos = new BlockPos(chunk.x * 16 + x, 0, chunk.z * 16 + z);
						Biome _biome = world.getBiome(blockPos);
						String blockBiome = _biome.getBiomeName();
						Boolean biomeFound = false;

						Iterator<Map.Entry<BiomeCC, Integer>> iterator = biomeTracker.entrySet().iterator();

						while (iterator.hasNext())
						{
							Map.Entry<BiomeCC, Integer> entry = iterator.next();
							String[] entryBiomes = entry.getKey().minecraftBiomes;

							for (int k = 0; k < entryBiomes.length; k++)
							{
								if(blockBiome.equals(entryBiomes[k]))
								{
									entry.setValue(entry.getValue() + 1);
									biomeFound = true;
									break;
								}
							}
						}
					}
				}
			}
		}

		BiomeCC currentBiome = plains;
		int currentMax = 0;

		for (Map.Entry<BiomeCC, Integer> entry : biomeTracker.entrySet())
		{
			if(entry.getValue() > currentMax)
			{
				currentMax = entry.getValue();
				currentBiome = entry.getKey();
			}
		}

		CCLog.logger.info(currentBiome.name);

		return currentBiome;
	}

	private ArrayList<TileEnums.Terrain> generateTerrain()
	{
		ArrayList<Terrain> terrain = new ArrayList<Terrain>();
		return terrain;
	}

	private ArrayList<TileEnums.Structure> generateStructures()
	{
		ArrayList<Structure> structures = new ArrayList<Structure>();
		return structures;
	}

	private TreeMap<Yield, Integer> generateYield()
	{
		return null;
	}

	public TilePos getPos()
	{
		return pos;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setLong("pos", pos.toLong());
		nbt = owner.writeToNBT(nbt);
		if(biome != null)
		{
			nbt.setString("biome", biome.name);
		}

		int size = terrain.size();
		nbt.setInteger("terrainSize", size);
		NBTTagList terrainNBT = new NBTTagList();

		for (int i = 0; i < size; i++)
		{
			NBTTagString ter = new NBTTagString(terrain.get(i).name());
			terrainNBT.appendTag(ter);
		}

		nbt.setTag("terrain", terrainNBT);

		size = structures.size();
		nbt.setInteger("structuresSize", size);
		NBTTagList structuresNBT = new NBTTagList();

		for (int i = 0; i < size; i++)
		{
			NBTTagString str = new NBTTagString(structures.get(i).name());
			structuresNBT.appendTag(str);
		}

		nbt.setTag("structures", structuresNBT);

		nbt.setTag("tileBorder", tileBorder.writeToNBT(new NBTTagCompound()));

		return nbt;
	}

	public static Tile readFromNBT(NBTTagCompound nbt, World world)
	{
		TilePos _pos = TilePos.fromLong(nbt.getLong("pos"));
		TileOwner _owner = TileOwner.readFromNBT(nbt);
		BiomeCC _biome = BiomeCC.BIOMES.get(nbt.getString("biome"));

		int size = nbt.getInteger("terrainSize");
		ArrayList<TileEnums.Terrain> _terrain = new ArrayList<TileEnums.Terrain>();
		NBTTagList ter = nbt.getTagList("terrain", NBTUtil.NBTType.NBTTagString.i);

		for (int i = 0; i < size; i++)
		{
			_terrain.add(TileEnums.Terrain.valueOf(ter.getStringTagAt(i)));
		}

		size = nbt.getInteger("structuresSize");
		ArrayList<TileEnums.Structure> _structures = new ArrayList<TileEnums.Structure>();
		NBTTagList str = nbt.getTagList("structures", NBTUtil.NBTType.NBTTagString.i);

		for (int i = 0; i < size; i++)
		{
			_structures.add(TileEnums.Structure.valueOf(str.getStringTagAt(i)));
		}

		TileBorder _tileBorder = TileBorder.readFromNBT(nbt.getCompoundTag("tileBorder"), world);

		Tile tile = new Tile(_pos, world, _owner, _biome, _terrain, _structures, _tileBorder);
		tile.tileBorder.addTile(tile);

		return tile;
	}

	public World getWorld()
	{
		return world;
	}

	public TileBorder getTileBorder()
	{
		return tileBorder;
	}

	public BiomeCC getBiome()
	{
		return biome;
	}

	public ArrayList<Terrain> getTerrain()
	{
		return terrain;
	}

	public ArrayList<Structure> getStructures()
	{
		return structures;
	}

	public TreeMap<Resource, Integer> getResources()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(!server.isCallingFromMinecraftThread())
		{
			throw new IllegalStateException("");
		}

		return resources;
	}

	public TileOwner getOwner()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(!server.isCallingFromMinecraftThread())
		{
			throw new IllegalStateException("");
		}

		return owner;
	}

	public void addWatcher(EntityPlayerMP player)
	{
		if(!forced)
		{
			force();
		}

		watchers.add(player);
	}

	public void removeWatcher(EntityPlayerMP player)
	{
		watchers.remove(player);

		if(forced && watchers.size() == 0)
		{
			unforce();
		}
	}
}
