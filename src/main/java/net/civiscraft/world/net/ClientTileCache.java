package net.civiscraft.world.net;

import java.io.IOException;

import net.civiscraft.lib.net.MessageManager;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.net.cache.CCClientObjectCaches.CacheType;
import net.civiscraft.lib.net.cache.ClientObjectCache;
import net.civiscraft.lib.net.cache.MessageObjectCacheRequest;
import net.civiscraft.lib.net.cache.MessageObjectCacheResponse;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.client.tile.ClientTilePos;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.entity.player.EntityPlayerMP;

public class ClientTileCache extends ClientObjectCache<ClientTile, ClientTilePos>
{
	public ClientTileCache()
	{
		super(null);
	}

	@Override
	protected ClientObjectCache<ClientTile, ClientTilePos>.ClientView generateClient()
	{
		return new CTileClientView();
	}

	@Override
	protected ClientObjectCache<ClientTile, ClientTilePos>.ServerView generateServer()
	{
		return new CTileServerView();
	}

	@Override
	protected ClientTile getCanonical(ClientTile obj)
	{
		return new ClientTile(obj);
	}

	@Override
	protected void writeObject(ClientTile obj, PacketBufferCC buffer)
	{
		obj.bufferWrite(buffer);
	}

	@Override
	protected ClientTile readObject(PacketBufferCC buffer) throws IOException
	{
		return ClientTile.bufferRead(buffer);
	}

	@Override
	protected ClientObjectCache<ClientTile, ClientTilePos>.Link clientRetrieve(ClientTilePos id)
	{
		CTileLink current = (CTileLink) clientObjects.get(id);
		if(current == null)
		{
			current = new CTileLink(id);
			clientUnknowns.add(current);
			clientObjects.put(id, current);
		}
		
		if(current.get() == null)
		{
			clientUnknowns.add(current);
		}
		
		return current;
	}

	@Override
	public void onClientWorldTick()
	{
		for(ClientObjectCache<ClientTile, ClientTilePos>.Link link : clientUnknowns)
		{
			if(link.id == null)
			{
				clientUnknowns.remove(link);
			}
		}
		
		ClientTilePos[] ids = new ClientTilePos[clientUnknowns.size()];
		for(int i = 0; i < ids.length; i++)
		{
			ids[i] = clientUnknowns.remove().id;
		}
		
		if(ids.length > 0)
		{
			try
			{
				MessageManager.sendToServer(new MessageObjectCacheRequest<ClientTilePos>(ids, CacheType.CTILE));
			}
			catch (Exception e)
			{
				
			}
		}
	}
	
	public class CTileServerView extends ServerView
	{
		@Override
		public void processDataRequest(PacketBufferCC buffer, EntityPlayerMP player)
		{
			ClientTilePos[] pArray = new ClientTilePos[1];
			byte[][] vArray = new byte[1][];
			
			pArray[0] = ClientTilePos.readBuffer(buffer);
			TileList list = TileList.get(player.world);
			ClientTile cTile = list.getClientTile(pArray[0].pos, player);
			vArray[0] = cTile.toBytes();
			store(cTile);
			MessageManager.sendTo(new MessageObjectCacheResponse<ClientTilePos>(pArray, vArray, CacheType.CTILE), player);
		}
	}
	
	public class CTileClientView extends ClientView
	{}
	
	public class CTileLink extends Link
	{
		public CTileLink(ClientTilePos id)
		{
			super(id);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj.getClass() == ClientTilePos.class) {return (ClientTilePos) obj == id;}
			
			return get().equals(obj);
		}
	}
	
	@Override
	public ClientTilePos nextId(ClientTile tile)
	{
		return tile.getClientTilePos();
	}

	@Override
	public MessageObjectCacheResponse<ClientTilePos> getResponse(Object[] ids, byte[][] values)
	{
		return new MessageObjectCacheResponse<ClientTilePos>(ids, values, CacheType.CTILE);
	}

	@SuppressWarnings("unchecked")
	public ClientObjectCache<ClientTile, ClientTilePos>.Link[] getTilesInRange(ClientTilePos currentTile, int range)
	{
		TilePos[] posArray = currentTile.pos.getTilesInRange(range);
		ClientObjectCache<ClientTile, ClientTilePos>.Link[] tileArray = new ClientObjectCache.Link[posArray.length];
		
		for(int i=0; i<posArray.length; i++)
		{
			ClientTilePos pos = new ClientTilePos(posArray[i], currentTile.playerId);
			tileArray[i] = client().retrieve(pos);
		}
		
		return tileArray;
	}
}
