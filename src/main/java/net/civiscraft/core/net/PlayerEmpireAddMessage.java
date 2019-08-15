package net.civiscraft.core.net;

import io.netty.buffer.ByteBuf;
import net.civiscraft.core.empire.ClientEmpire;
import net.civiscraft.core.empire.Empire;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PlayerEmpireAddMessage implements IMessage
{
	public PlayerEmpireAddMessage()
	{
	}

	public ClientEmpire empire;

	public PlayerEmpireAddMessage(ClientEmpire empire)
	{
		this.empire = empire;
	}

	public PlayerEmpireAddMessage(Empire empire)
	{

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
