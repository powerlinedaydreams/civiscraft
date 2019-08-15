package net.civiscraft.world.event;

import java.util.ArrayList;

import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileWatchEvent extends Event
{
	private final TilePos tile;
	private final ArrayList<TilePos> tiles;
	public final boolean multiple;
	private final EntityPlayerMP player;

	public TileWatchEvent(TilePos tile, EntityPlayerMP player)
	{
		this.tile = tile;
		this.tiles = null;
		this.multiple = false;
		this.player = player;
	}

	public TileWatchEvent(ArrayList<TilePos> tiles, EntityPlayerMP player)
	{
		this.tile = null;
		this.tiles = tiles;
		this.multiple = true;
		this.player = player;
	}

	public TilePos getTile()
	{
		return tile;
	}

	public ArrayList<TilePos> getTiles()
	{
		return tiles;
	}

	public EntityPlayerMP getPlayer()
	{
		return player;
	}

	public static class Watch extends TileWatchEvent
	{
		public Watch(TilePos tile, EntityPlayerMP player)
		{
			super(tile, player);
		}

		public Watch(ArrayList<TilePos> tiles, EntityPlayerMP player)
		{
			super(tiles, player);
		}
	}

	public static class UnWatch extends TileWatchEvent
	{
		public UnWatch(TilePos tile, EntityPlayerMP player)
		{
			super(tile, player);
		}

		public UnWatch(ArrayList<TilePos> tiles, EntityPlayerMP player)
		{
			super(tiles, player);
		}
	}

	public static class Seen extends TileWatchEvent
	{
		public Seen(TilePos tile, EntityPlayerMP player)
		{
			super(tile, player);
		}

		public Seen(ArrayList<TilePos> tiles, EntityPlayerMP player)
		{
			super(tiles, player);
		}
	}
}
