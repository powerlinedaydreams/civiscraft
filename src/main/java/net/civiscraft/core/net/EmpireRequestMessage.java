package net.civiscraft.core.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class EmpireRequestMessage implements IMessage
{
	public EmpireRequestMessage()
	{

	}

	public boolean isNewEmpire;

	public EmpireRequestMessage(boolean isNewEmpire)
	{
		this.isNewEmpire = isNewEmpire;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		isNewEmpire = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(isNewEmpire);
	}
}
