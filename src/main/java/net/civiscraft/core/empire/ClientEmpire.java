package net.civiscraft.core.empire;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.entity.player.EntityPlayer;

public class ClientEmpire
{
	public final UUID id;
	@Nullable
	public String name;
	@Nullable
	private UUID player;
	private boolean isExtant;
	private Map<TilePos, Integer> tiles = new TreeMap<TilePos, Integer>();

	public ClientEmpire(UUID id, String name, UUID player, boolean isExtant, Map<TilePos, Integer> tiles)
	{
		this.id = id;
		this.name = name;
		this.player = player;
		this.isExtant = isExtant;
		this.tiles = tiles;
	}

	public ClientEmpire(Empire empire, EntityPlayer player)
	{
		PlayerIntel intel = (PlayerIntel) player.getCapability(CapPlayerIntel.CAP, null);
		this.id = empire.id;
		this.name = intel.getEmpires().contains(this.id) ? empire.name : null;
		this.player = empire.getPlayer();
		this.isExtant = empire.exists();
		for (Map.Entry<TilePos, Integer> entry : empire.getTiles().entrySet())
		{
			if(intel.getSeenTiles().contains(entry.getKey()))
			{
				this.tiles.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public UUID getPlayer()
	{
		return player;
	}

	public boolean exists()
	{
		return isExtant;
	}

	public Map<TilePos, Integer> getTiles()
	{
		return tiles;
	}

	public static ClientEmpire fromBytes(ByteBuf buf)
	{
		UUID id_ = new UUID(buf.readLong(), buf.readLong());
		String name_ = null;
		if(buf.readBoolean())
		{
			int length = buf.readInt();
			name_ = buf.readCharSequence(length, Charset.forName("UTF-8")).toString();
		}

		UUID player_ = null;

		if(buf.readBoolean())
		{
			player_ = new UUID(buf.readLong(), buf.readLong());
		}

		boolean isExtant_ = buf.readBoolean();

		Map<TilePos, Integer> tiles_ = new TreeMap<TilePos, Integer>();

		int tileLength = buf.readInt();
		for (int i = 0; i < tileLength; i++)
		{
			TilePos pos = TilePos.fromLong(buf.readLong());
			int time = buf.readInt();
			tiles_.put(pos, time);
		}

		return new ClientEmpire(id_, name_, player_, isExtant_, tiles_);
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(id.getMostSignificantBits());
		buf.writeLong(id.getLeastSignificantBits());

		buf.writeBoolean(name != null);
		if(name != null)
		{
			buf.writeInt(name.length());
			buf.writeCharSequence(name, Charset.forName("UTF-8"));
		}

		buf.writeBoolean(player != null);
		if(player != null)
		{
			buf.writeLong(player.getMostSignificantBits());
			buf.writeLong(player.getLeastSignificantBits());
		}

		buf.writeBoolean(isExtant);

		buf.writeInt(tiles.size());
		for (Map.Entry<TilePos, Integer> entry : tiles.entrySet())
		{
			buf.writeLong(entry.getKey().toLong());
			buf.writeInt(entry.getValue());
		}
	}
}
