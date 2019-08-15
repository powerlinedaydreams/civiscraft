package net.civiscraft.world.client.tile;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.civiscraft.lib.util.data.ICacheable;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.entity.player.EntityPlayer;

public class ClientTilePos implements ICacheable<ClientTilePos>
{
	public final TilePos pos;
	public final UUID playerId;

	public ClientTilePos(TilePos pos, EntityPlayer player)
	{
		this.pos = pos;
		this.playerId = player.getUniqueID();
	}

	public ClientTilePos(TilePos pos, UUID playerId)
	{
		this.pos = pos;
		this.playerId = playerId;
	}

	@Override
	public void writeBuffer(ByteBuf buffer)
	{
		// TODO Auto-generated method stub

	}

	public static ClientTilePos readBuffer(ByteBuf buffer)
	{
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj.getClass() != getClass())
		{
			return false;
		}

		ClientTilePos object = (ClientTilePos) obj;

		return pos == object.pos && playerId == object.playerId;
	}

	@Override
	public int hashCode()
	{
		return ((Long) pos.toLong()).hashCode() ^ playerId.hashCode();
	}
}
