package net.civiscraft.world.map.tile;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import net.civiscraft.core.empire.Empire;
import net.civiscraft.lib.util.NBTUtil.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

public class TileOwner
{
	public final TilePos pos;
	public boolean isOwned;
	private UUID owner;
	private ArrayList<UUID> empires;
	private ArrayList<Double> influence;

	public TileOwner(Tile tile)
	{
		pos = tile.getPos();
		isOwned = false;
		empires = new ArrayList<UUID>();
		influence = new ArrayList<Double>();
	}

	public TileOwner(TilePos pos, boolean isOwned, @Nullable UUID owner)
	{
		this.pos = pos;
		this.isOwned = isOwned;

		if(owner != null)
		{
			this.owner = owner;
		}
	}

	private TileOwner(TilePos pos, boolean isOwned, ArrayList<UUID> empires, ArrayList<Double> influence)
	{
		this.pos = pos;
		this.isOwned = isOwned;
		this.empires = empires;
		this.influence = influence;
	}

	private TileOwner(TilePos pos, boolean unknown)
	{
		if(unknown == false)
		{
			throw new IllegalArgumentException("This constructor is only for creating UNKNOWN TileOwners");
		}

		this.pos = pos;
		this.isOwned = true;
		this.owner = Empire.nullEmpire;
	}

	private void checkOwner()
	{
		UUID emp = null;
		double inf = 0d;
		int size = influence.size();

		for (int i = 0; i < size; i++)
		{
			double check = influence.get(i);

			if(check > inf)
			{
				inf = check;
				emp = empires.get(i);
			}
		}

		if(inf > 10d)
		{
			this.owner = emp;
			this.isOwned = true;
		}
	}

	public void updateInfluence(UUID empire, double inf)
	{
		int index = empires.indexOf(empire);

		if(index != -1)
		{
			influence.set(index, influence.get(index) + inf);

			if(empire != owner)
			{
				checkOwner();
			}
		}

		else
		{
			empires.add(empire);
			influence.add(inf);
			checkOwner();
		}
	}

	public void removeEmpire(UUID empire)
	{
		int index = empires.indexOf(empire);
		empires.remove(index);
		influence.remove(index);

		checkOwner();
	}

	public static TileOwner readFromNBT(NBTTagCompound nbt)
	{
		TilePos _pos = TilePos.fromLong(nbt.getLong("pos"));
		boolean _isOwned = nbt.getBoolean("isOwned");
		ArrayList<UUID> _empires = new ArrayList<UUID>();
		ArrayList<Double> _influence = new ArrayList<Double>();
		int size = nbt.getInteger("ownerSize");
		NBTTagList empireNBT = nbt.getTagList("empires", NBTType.NBTTagCompound.i);
		NBTTagList influenceNBT = nbt.getTagList("influence", NBTType.NBTTagDouble.i);

		for (int i = 0; i < size; i++)
		{
			_empires.add(empireNBT.getCompoundTagAt(i).getUniqueId("uuid"));
			_influence.add(influenceNBT.getDoubleAt(i));
		}

		return new TileOwner(_pos, _isOwned, _empires, _influence);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setLong("pos", pos.toLong());
		nbt.setBoolean("isOwned", isOwned);
		int size = empires.size();
		nbt.setInteger("ownerSize", size);

		NBTTagList empireNBT = new NBTTagList();
		NBTTagList influenceNBT = new NBTTagList();

		for (int i = 0; i < size; i++)
		{
			UUID empire = empires.get(i);
			double inf = influence.get(i);

			NBTTagCompound empireTag = new NBTTagCompound();
			empireTag.setUniqueId("uuid", empire);
			NBTTagDouble influenceTag = new NBTTagDouble(inf);

			empireNBT.appendTag(empireTag);
			influenceNBT.appendTag(influenceTag);
		}

		nbt.setTag("empires", empireNBT);
		nbt.setTag("influence", influenceNBT);

		return nbt;
	}

	public UUID getOwner()
	{
		return owner;
	}

	public static TileOwner unknown(TilePos pos)
	{
		return new TileOwner(pos, true);
	}
}
