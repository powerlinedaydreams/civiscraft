package net.civiscraft.world.net;

import io.netty.buffer.ByteBuf;
import net.civiscraft.world.client.tile.ClientTile;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TileAddMessage implements IMessage
{
	public TileAddMessage()
	{
	}

	public ClientTile tile;

	public TileAddMessage(ClientTile tile)
	{
		this.tile = tile;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		tile = ClientTile.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		tile.toBytes(buf);
	}

}
