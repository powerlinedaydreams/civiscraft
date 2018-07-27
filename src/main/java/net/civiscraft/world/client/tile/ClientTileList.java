package net.civiscraft.world.client.tile;

import net.civiscraft.lib.client.ClientDataList;
import net.civiscraft.lib.net.cache.NetworkedObjectCache.Link;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.net.NetworkedTileCache;

public class ClientTileList extends ClientDataList<ClientTile, NetworkedTileCache, TilePos>
{

	public ClientTileList()
	{
		super(new ClientTile());
	}

	public Link[] getTilesInRange(TilePos currentTile, int range)
	{
		TilePos[] posArray = currentTile.getTilesInRange(range);
		Link[] tileArray = new Link[posArray.length];
		
		for(int i=0; i<posArray.length; i++)
		{
			tileArray[i] = getItem(posArray[i]);
		}
		
		return tileArray;
	}
}
