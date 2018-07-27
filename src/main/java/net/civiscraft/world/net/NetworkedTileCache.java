package net.civiscraft.world.net;

import java.io.IOException;

import net.civiscraft.lib.net.MessageManager;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.net.cache.MessageObjectCacheResponse;
import net.civiscraft.lib.net.cache.NetworkedObjectCache;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.entity.player.EntityPlayerMP;

public class NetworkedTileCache extends NetworkedObjectCache<ClientTile>
{
	
	public NetworkedTileCache()
	{
		super(new ClientTile(), "clienttilecache");
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
	protected String getCacheName()
	{
		return "ClientTile";
	}
	
	public class ServerView
	{
		public void onTileWatch(TilePos tile, EntityPlayerMP player)
		{
			MessageManager.sendTo(new MessageObjectCacheResponse(name, null, null), player);
		}
		
		public int getID(TilePos pos)
		{
			//It is checked, it just doesn't know it
			@SuppressWarnings("unchecked")
			Integer current = serverObjectToID.get(pos);
			
			if(current == null) {return -1;}
			
			else {return current;}
		}
		
		public void processRequest(PacketBufferCC buf, EntityPlayerMP player)
		{
			TilePos pos = TilePos.fromLong(buf.readLong());
			TileList list = TileList.get(player.world);
			list.getClientTile(pos, player);
		}
	}
}
