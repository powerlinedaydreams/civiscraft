package net.civiscraft.lib.util.data;

import java.lang.reflect.Array;

import io.netty.buffer.ByteBuf;

public interface ICacheable<T extends ICacheable<T>>
{
	public static <T> T[] createArray(Class<T> c, int size)
	{
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(c, size);
		return array;
	}
	
	public abstract void writeBuffer(ByteBuf buffer);
}
