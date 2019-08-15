package net.civiscraft.world.event;

import net.civiscraft.world.map.tile.TileOwner;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileUpdateEvent extends Event
{
	private final TilePos tile;

	protected TileUpdateEvent(TilePos tile)
	{
		this.tile = tile;
	}

	public TilePos getTile()
	{
		return tile;
	}

	public static class Border extends TileUpdateEvent
	{
		private boolean isComplete;

		public Border(TilePos tile, boolean isComplete)
		{
			super(tile);
			this.isComplete = isComplete;
		}

		public boolean isComplete()
		{
			return isComplete;
		}
	}

	public static class Owner extends TileUpdateEvent
	{
		private final TileOwner oldO;
		private final TileOwner newO;

		public Owner(TilePos tile, TileOwner oldO, TileOwner newO)
		{
			super(tile);
			this.oldO = oldO;
			this.newO = newO;
		}

		public TileOwner getOldOwner()
		{
			return oldO;
		}

		public TileOwner getNewOwner()
		{
			return newO;
		}
	}

	public static class Structure extends TileUpdateEvent
	{
		private final boolean con;
		private final Structure str;

		public Structure(TilePos tile, boolean constructOrDestroy, Structure str)
		{
			super(tile);
			this.con = constructOrDestroy;
			this.str = str;
		}

		public boolean wasBuilt()
		{
			return con;
		}

		public Structure getStructure()
		{
			return str;
		}
	}
}
