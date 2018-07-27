package net.civiscraft.lib.net.cache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.net.cache.CCClientObjectCaches.CacheType;
import net.civiscraft.lib.util.data.ICacheable;
import net.civiscraft.world.client.tile.ClientTilePos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class MessageObjectCacheRequest<I extends ICacheable<I>> implements IMessage
{
	private CacheType cacheType;
	private I[] ids;
	
	public MessageObjectCacheRequest() {}
	
	public MessageObjectCacheRequest(I[] ids, CacheType cacheType)
	{
		this.ids = ids;
		this.cacheType = cacheType;
		if(ids.length > Short.MAX_VALUE)
		{
			throw new IllegalStateException("Tried to request too many IDs (" + ids.length + ")");
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeShort(cacheType.getValue());
		int size = ids.length;
		buf.writeShort(size);
		
		for(int i=0; i<size; i++)
		{
			ids[i].writeBuffer(buf);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(ByteBuf buf)
	{
		cacheType = CacheType.valueOf(buf.readShort());
		int size = buf.readShort();

		switch(cacheType)
		{
		case CTILE:
		{
			ids = (I[]) new ClientTilePos[size];
				
			for(int i=0; i<size; i++)
			{
				ids[i] = (I) ClientTilePos.readBuffer(buf);
			}
		}
		}
	}
	
	@SuppressWarnings({ "rawtypes"})
	public static final IMessageHandler<MessageObjectCacheRequest, MessageObjectCacheResponse> HANDLER = (message, ctx) -> 
	{
		ClientObjectCache<?, ?> cache = CCClientObjectCaches.get(message.cacheType);
		byte[][] values = new byte[message.ids.length][];
		
		PacketBufferCC buffer = new PacketBufferCC(Unpooled.buffer());
		for(int i=0; i<values.length; i++)
		{
			ICacheable<?> id = message.ids[i];
			cache.writeObjectServer(id, buffer);
			values[i] = new byte[buffer.readableBytes()];
			buffer.readBytes(values[i]);
			buffer.clear();
		}
		return cache.getResponse(message.ids, values);
	};
}
