package net.civiscraft.core.net;

import io.netty.buffer.ByteBuf;
import net.civiscraft.core.empire.ClientEmpire;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class EmpireUpdateMessage implements IMessage
{
	public EmpireUpdateMessage()
	{
	}

	public ClientEmpire empire;

	public EmpireUpdateMessage(ClientEmpire empire)
	{
		this.empire = empire;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		empire = ClientEmpire.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		empire.toBytes(buf);
	}
}
