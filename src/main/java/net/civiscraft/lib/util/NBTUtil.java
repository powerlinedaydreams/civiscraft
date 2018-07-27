package net.civiscraft.lib.util;

import net.minecraft.util.math.ChunkPos;

public class NBTUtil
{
	public enum NBTType
	{
		NBTTagEnd(0),
		NBTTagByte(1),
		NBTTagShort(2),
		NBTTagInt(3),
		NBTTagLong(4),
		NBTTagFLoat(5),
		NBTTagDouble(6),
		NBTTagByteArray(7),
		NBTTagString(8),
		NBTTagList(9),
		NBTTagCompound(10),
		NBTTagIntArray(11),
		NBTTagLongArray(12);
		
		public int i;
		
		private NBTType(int i)
		{
			this.i = i;
		}
	}
}
