package net.civiscraft.core.net;

import io.netty.buffer.ByteBuf;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TileClaimMessage implements IMessage
{
	public TileClaimMessage()
	{

	}

	public TilePos tilePos;

	public TileClaimMessage(TilePos pos)
	{
		tilePos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		tilePos = TilePos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(tilePos.toLong());
	}
}
