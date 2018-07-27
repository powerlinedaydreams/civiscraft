package net.civiscraft.lib.net;

import java.io.IOException;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IMessageHandlerCC
{
	IMessage handleMessage(MessageContext ctx, PacketBufferCC buffer) throws IOException;
}
