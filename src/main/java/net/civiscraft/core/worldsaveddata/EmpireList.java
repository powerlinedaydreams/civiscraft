package net.civiscraft.core.worldsaveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.civiscraft.core.empire.Empire;
import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.util.NBTUtil.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class EmpireList extends WorldSavedData
{
	private static final String DATA_NAME = CCLib.MODID + "_EmpireList";
	private Map<UUID, Empire> empires = new HashMap<UUID, Empire>();
	private static World world;

	public EmpireList()
	{
		super(DATA_NAME);
	}

	public EmpireList(String s)
	{
		super(s);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList xList = nbt.getTagList("empires", NBTType.NBTTagCompound.i);
		empires = new HashMap<UUID, Empire>();

		for (int i = 0; i < xList.tagCount(); i++)
		{
			NBTTagCompound entry = xList.getCompoundTagAt(i);

			Empire empire = Empire.readFromNBT(entry, world);
			empires.put(empire.id, empire);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();

		for (Map.Entry<UUID, Empire> entry : empires.entrySet())
		{
			NBTTagCompound tag = entry.getValue().writeToNBT(new NBTTagCompound());
			list.appendTag(tag);
		}

		nbt.setTag("empires", list);
		return nbt;
	}

	public static EmpireList get(World world)
	{
		MapStorage storage = world.getMapStorage();
		EmpireList instance = (EmpireList) storage.getOrLoadData(EmpireList.class, DATA_NAME);
		EmpireList.world = world;

		if(instance == null)
		{
			instance = new EmpireList();
			storage.setData(DATA_NAME, instance);
			CCLog.logger.info("New EmpireList created");
		}

		return instance;
	}

	public void addEmpire(Empire empire)
	{
		this.markDirty();
		empires.put(empire.id, empire);
	}

	public Empire getEmpire(UUID id)
	{
		return empires.get(id);
	}

	public Set<Entry<UUID, Empire>> getEmpires()
	{
		return empires.entrySet();
	}

	public void removeEmpire(UUID id)
	{
		empires.remove(id);
	}
}
