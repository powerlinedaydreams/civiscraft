package net.civiscraft.world.resource;

import java.util.HashMap;

import net.civiscraft.lib.net.PacketBufferCC;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class Resource
{
	public static HashMap<String, Resource> RESOURCES = new HashMap<String, Resource>();

	public String name;

	public Resource(String name)
	{
		this.name = name;
		RESOURCES.put(name, this);
	}

	public void bufferWrite(PacketBufferCC buffer)
	{

	}

	public static Resource bufferRead(PacketBufferCC buffer)
	{
		return null;
	}

	public NBTBase writeNBT()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static Resource readNBT(NBTTagCompound tag)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
