package net.civiscraft.core.empire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.civiscraft.core.event.EmpireEvent;
import net.civiscraft.core.worldsaveddata.EmpireList;
import net.civiscraft.lib.util.NBTUtil;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * Empires are the core of Civiscraft functionality. They have players as
 * citizens, control cities and tiles, gather resources, conduct diplomacy with
 * other empires, and wage war.
 * 
 * @author christopherstockus
 *
 */
public class Empire
{
	/**
	 * The hard-coded null value for UUID, as a UUID cannot be null.
	 */
	public static final UUID NULL = UUID.fromString("00000000-0000-0000-0000-000000000000");

	/**
	 * The UUID for the empire.
	 */
	public final UUID id;
	/**
	 * The name of the empire.
	 */
	public String name = null;
	/**
	 * Whether the empire is still extant.
	 */
	private boolean isExtant = false;

	/**
	 * List of citizen UUIDs.
	 */
	private List<UUID> players = null;
	/**
	 * Map of tiles that are under the empire's control.
	 */
	private Map<TilePos, Integer> tiles = null;

	/**
	 * Creates a new, empty Empire without name, citizens, or territory.
	 * 
	 * @param id
	 *            The UUID for the empire.
	 * @param world
	 *            The Minecraft World this empire exists in. Used to register the
	 *            empire.
	 */
	public Empire(UUID id, World world)
	{
		this.id = id;
		this.isExtant = true;
		EmpireList empireList = EmpireList.get(world);
		empireList.addEmpire(this);
		MinecraftForge.EVENT_BUS.post(new EmpireEvent.Create(this));
	}

	/**
	 * Creates a new Empire. Used when loading empires from WorldSavedData.
	 * 
	 * @param id
	 *            The UUID for the empire.
	 * @param name
	 *            The name for the empire.
	 * @param players
	 *            The list of citizen's UUIDs.
	 * @param tiles
	 *            Map of the tiles in the empire's control.
	 */
	public Empire(UUID id, String name, List<UUID> players, Map<TilePos, Integer> tiles)
	{
		this.id = id;
		this.name = name;
		this.players = players;
		this.isExtant = true;
		this.tiles = tiles;
		MinecraftForge.EVENT_BUS.post(new EmpireEvent.Load(this));
	}

	/**
	 * 
	 * @return List of citizen's UUIDs.
	 */
	public List<UUID> getPlayers()
	{
		return players;
	}

	/**
	 * Adds a player to the list of player-citizens in the Empire
	 * 
	 * @param playerId
	 *            The UUID of the player
	 */
	public void addPlayer(UUID playerId)
	{
		this.players.add(playerId);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addTerritory(TilePos tile)
	{
		this.tiles.put(tile, 0);
	}

	/**
	 * 
	 * @return Whether the empire is extant.
	 */
	public boolean exists()
	{
		return isExtant;
	}

	/**
	 * 
	 * @return Map of the tiles in the empire's control.
	 */
	public Map<TilePos, Integer> getTiles()
	{
		return tiles;
	}

	/**
	 * "Deletes" this empire by marking it as non-extant
	 * 
	 * @param world
	 *            The Minecraft World this empire is in. Used to post an empire
	 *            deletion event to the event bus.
	 */
	public void delete(World world)
	{
		isExtant = false;
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new EmpireEvent.Delete(this, world));
	}

	/**
	 * Why would you want to use this? This is only for loading Empire data from
	 * WorldSavedData.
	 * 
	 * @param nbt
	 *            The NBTTagCompound containing this empire's data.
	 * @param world
	 *            The Minecraft World this empire is in. Not currently used.
	 * @return This empire.
	 */
	public static Empire readFromNBT(NBTTagCompound nbt, World world)
	{
		UUID _id = nbt.getUniqueId("id");
		boolean hasName = nbt.getBoolean("name?");
		String _name = hasName ? nbt.getString("name") : null;

		boolean hasPlayer = nbt.getBoolean("players?");
		List<UUID> _players = hasPlayer ? new ArrayList<UUID>() : null;

		if(hasPlayer)
		{
			int playerSize = nbt.getInteger("playersSize");
			NBTTagList playerTags = nbt.getTagList("players", NBTUtil.NBTType.NBTTagCompound.i);

			for (int i = 0; i < playerSize; i++)
			{
				NBTTagCompound tag = playerTags.getCompoundTagAt(i);
				_players.add(new UUID(tag.getLong("long1"), tag.getLong("long2")));
			}
		}

		boolean hasTile = nbt.getBoolean("tiles?");
		Map<TilePos, Integer> _tiles = hasTile ? new HashMap<TilePos, Integer>() : null;

		if(hasTile)
		{
			int tileSize = nbt.getInteger("tileSize");
			NBTTagList tileTags = nbt.getTagList("tiles", NBTUtil.NBTType.NBTTagCompound.i);

			for (int i = 0; i < tileSize; i++)
			{
				NBTTagCompound tag = tileTags.getCompoundTagAt(i);
				_tiles.put(TilePos.fromLong(tag.getLong("pos")), tag.getInteger("time"));
			}
		}

		return new Empire(_id, _name, _players, _tiles);
	}

	/**
	 * Why would you want to use this? This is only for saving Empire data to
	 * WorldSavedData.
	 * 
	 * @param nbt
	 *            The NBTTagCompound to which this empire's data is being written.
	 * @return The NBTTagCompound with the empire's data included.
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setUniqueId("id", id);
		nbt.setBoolean("name?", name != null);
		if(name != null)
		{
			nbt.setString("name", name);
		}

		nbt.setBoolean("players?", players != null);
		if(players != null)
		{
			nbt.setInteger("playerSize", players.size());
			NBTTagList playerTags = new NBTTagList();
			for (UUID player : players)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("long1", player.getMostSignificantBits());
				tag.setLong("long2", player.getLeastSignificantBits());
				playerTags.appendTag(tag);
			}
		}

		nbt.setBoolean("tiles?", tiles != null);
		if(tiles != null)
		{
			nbt.setInteger("tileSize", tiles.size());
			NBTTagList tileTags = new NBTTagList();
			for (Map.Entry<TilePos, Integer> entry : tiles.entrySet())
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("pos", entry.getKey().toLong());
				tag.setInteger("time", entry.getValue());
				tileTags.appendTag(tag);
			}
		}

		return nbt;
	}
}
