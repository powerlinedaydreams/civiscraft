package net.civiscraft.lib.net.cache;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.net.cache.CCClientObjectCaches.CacheType;
import net.civiscraft.lib.util.data.ICacheable;
import net.civiscraft.world.CCWorldProxy;
import net.civiscraft.world.client.tile.ClientTilePos;
import net.civiscraft.world.net.ClientTileCache;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class MessageObjectCacheResponse<I extends ICacheable<I>> implements IMessage
{	
	private CacheType cacheType;
	private I[] ids;
	private byte[][] values;
	
	public MessageObjectCacheResponse() {}
	
	@SuppressWarnings("unchecked")
	public MessageObjectCacheResponse(Object[] idsIn, byte[][] values, CacheType type)
	{
		I[] ids = (I[]) idsIn; 
		this.ids = ids;
		this.values = values;
		this.cacheType = type;
		CCLog.logger.info("MessageObjectCacheResponse created"); ///////////////////////////////////
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeShort(cacheType.getValue());
		int size = ids.length;
		buf.writeInt(size);
		for(int i=0; i<size; i++)
		{
			ids[i].writeBuffer(buf);
			buf.writeShort(values[i].length);
			buf.writeBytes(values[i]);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(ByteBuf buf)
	{
		cacheType = CacheType.valueOf(buf.readShort());
		
		int size = buf.readInt();
		values = new byte[size][];
		
		switch(cacheType)
		{
		case CTILE:
		{
			ids = (I[]) new ClientTilePos[size];
			
			for(int i=0; i<size; i++)
			{
				ids[i] = (I) ClientTilePos.readBuffer(buf);
				values[i] = new byte[buf.readShort()];
				buf.readBytes(values[i]);
			}
		}
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static final IMessageHandler<MessageObjectCacheResponse, IMessage> HANDLER = (message, ctx) ->
	{
		CCLog.logger.info("MessageObjectCacheResponse received"); ///////////////////////////////////
		
		try 
		{
			switch(message.cacheType)
			{
			case CTILE:
				ClientTileCache cache = CCWorldProxy.TILE_CACHE;
				for(int i=0; i<message.ids.length; i++)
				{
					ClientTilePos pos = (ClientTilePos) message.ids[i];
					byte[] payload = message.values[i];
					cache.readObjectClient(pos, new PacketBufferCC(Unpooled.copiedBuffer(payload)));
				}
				
				return null;
			default:
				return null;
			}
		}
		
		catch (IOException io)
		{
			throw new Error(io);
		}
	};
}
