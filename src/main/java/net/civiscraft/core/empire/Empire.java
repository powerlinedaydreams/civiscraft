package net.civiscraft.core.empire;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.civiscraft.core.event.EmpireEvent;
import net.civiscraft.core.worldsaveddata.EmpireList;
import net.civiscraft.lib.util.NBTUtil;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class Empire
{
	public static final UUID nullEmpire = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public final UUID id;
	public String name;
	@Nullable
	private UUID player;
	private boolean isExtant;
	private Map<TilePos, Integer> tiles = new TreeMap<TilePos, Integer>();

	public Empire(UUID id, String name, UUID player, TilePos tile, World world)
	{
		this.id = id;
		this.name = name;
		this.player = player;
		this.isExtant = true;
		tiles.put(tile, 1);
		EmpireList empireList = EmpireList.get(world);
		empireList.addEmpire(this);
		TileList tileList = TileList.get(world);
		tileList.getTileByPos(tile).getOwner().updateInfluence(id, 100);
		MinecraftForge.EVENT_BUS.post(new EmpireEvent.Create(this));
	}

	public Empire(UUID id, String name, UUID player, Map<TilePos, Integer> tiles)
	{
		this.id = id;
		this.name = name;
		this.player = player;
		this.isExtant = true;
		this.tiles = tiles;
		MinecraftForge.EVENT_BUS.post(new EmpireEvent.Load(this));
	}

	public UUID getPlayer()
	{
		return player;
	}

	public boolean exists()
	{
		return isExtant;
	}

	public Map<TilePos, Integer> getTiles()
	{
		return tiles;
	}

	public static Empire readFromNBT(NBTTagCompound nbt, World world)
	{
		UUID _id = nbt.getUniqueId("id");
		String _name = nbt.getString("name");
		UUID _player = nbt.getUniqueId("player");

		int size = nbt.getInteger("tileSize");
		Map<TilePos, Integer> _tiles = new TreeMap<TilePos, Integer>();
		NBTTagList tileTags = nbt.getTagList("tiles", NBTUtil.NBTType.NBTTagCompound.i);

		for (int i = 0; i < size; i++)
		{
			NBTTagCompound tag = tileTags.getCompoundTagAt(i);
			_tiles.put(TilePos.fromLong(tag.getLong("pos")), tag.getInteger("time"));
		}

		return new Empire(_id, _name, _player, _tiles);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setUniqueId("id", id);
		nbt.setString("name", name);
		nbt.setUniqueId("player", player);

		nbt.setInteger("tileSize", tiles.size());
		NBTTagList tileTags = new NBTTagList();
		for (Map.Entry<TilePos, Integer> entry : tiles.entrySet())
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("pos", entry.getKey().toLong());
			tag.setInteger("time", entry.getValue());
			tileTags.appendTag(tag);
		}

		return nbt;
	}

	public static Empire fromBytes(ByteBuf buf)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void toBytes(ByteBuf buf)
	{
		// TODO Auto-generated method stub

	}

	public void delete(World world)
	{
		isExtant = false;
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new EmpireEvent.Delete(this, world));
	}
}
