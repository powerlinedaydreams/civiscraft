package net.civiscraft.lib.net;

@FunctionalInterface
public interface IMessageWriter
{
	void write(PacketBufferCC buffer);
}
