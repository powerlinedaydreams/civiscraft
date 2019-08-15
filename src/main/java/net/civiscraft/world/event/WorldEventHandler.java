package net.civiscraft.world.event;

import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.world.CCWorld;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.map.tile.Tile;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.net.TileAddMessage;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandler
{
	@SubscribeEvent
	public void onChunkWatch(ChunkWatchEvent.Watch e)
	{
		TilePos pos = new TilePos(e.getChunk());
		EntityPlayerMP player = e.getPlayer();

		PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);

		if(!player.world.isRemote && intel != null)
		{
			intel.addWatchedTile(pos);
		}
	}

	@SubscribeEvent
	public void onChunkUnWatch(ChunkWatchEvent.UnWatch e)
	{
		TilePos pos = new TilePos(e.getChunk());
		EntityPlayerMP player = e.getPlayer();
		PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);

		if(!player.world.isRemote && intel != null)
		{
			intel.removeWatchedTile(pos);
		}
	}

	@SubscribeEvent
	public void onTileWatch(TileWatchEvent.Watch e)
	{
		EntityPlayerMP player = e.getPlayer();
		World world = player.world;

		TileList list = TileList.get(world);
		TilePos pos = e.getTile();
		Tile tile = list.getTileByPos(pos);

		if(tile == null)
		{
			tile = new Tile(pos, world);
			list.addTile(tile);
		}

		tile.addWatcher(player);
		CCWorld.NETWORK_CHANNEL.sendTo(new TileAddMessage(new ClientTile(tile, player)), player);

	}

	@SubscribeEvent
	public void onTileUnWatch(TileWatchEvent.UnWatch e)
	{
		EntityPlayerMP player = e.getPlayer();
		TileList list = TileList.get(player.world);
		Tile tile = list.getTileByPos(e.getTile());

		tile.removeWatcher(player);
	}

	@SubscribeEvent
	public void onTileBorderUpdate(TileUpdateEvent.Border e)
	{
		if(e.isComplete())
		{

		}

		else
		{

		}
	}

	@SubscribeEvent
	public void onTileStructureUpdate(TileUpdateEvent.Structure e)
	{

	}

	@SubscribeEvent
	public void onTileOwnerUpdate(TileUpdateEvent.Owner e)
	{

	}
}
