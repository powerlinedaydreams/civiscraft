package net.civiscraft.lib.net.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.util.data.ICacheable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class ClientObjectCache<T, I extends ICacheable<I>>
{
	protected final T defaultObject;
	
	protected final BiMap<I, T> serverIDToObject = HashBiMap.create();
	protected final BiMap<T, I> serverObjectToID = serverIDToObject.inverse();
	
	protected final HashMap<I, Link> clientObjects = new HashMap<>();
	protected final Queue<Link> clientUnknowns = new LinkedList<>();
	
	private final ServerView serverView;
	private final ClientView clientView;
	
	public ClientObjectCache(T defaultObject)
	{
		this.defaultObject = defaultObject;
		this.serverView = generateServer();
		this.clientView = generateClient();
	}
	
	protected abstract ClientView generateClient();

	protected abstract ServerView generateServer();

	public ServerView server()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(!server.isCallingFromMinecraftThread())
		{
			throw new IllegalStateException("");
		}
		
		return serverView;
	}
	
	public ClientView client()
	{
		if(!Minecraft.getMinecraft().isCallingFromMinecraftThread())
		{
			throw new IllegalStateException("");
		}
		
		return clientView;
	}
	
	public abstract class ServerView
	{
		public ServerView() {}
		
		public I store(T value)
		{
			return serverStore(value);
		}
		
		public I getID(T value)
		{
			return serverGetID(value);
		}
		
		public abstract void processDataRequest(PacketBufferCC buffer, EntityPlayerMP player);
	}
	
	public abstract class ClientView
	{
		public ClientView() {}
		
		public Link retrieve (I id)
		{
			return clientRetrieve(id);
		}
	}
	
	public abstract class Link implements Supplier<T>
	{
		T actual;
		
		public final I id;
		
		public Link(I id)
		{
			this.id = id;
		}
		
		@Override
		public T get()
		{
			return actual == null ? defaultObject : actual;
		}
		
		public boolean hasBeenReceived()
		{
			return actual != null;
		}
		
		@Override
		public abstract boolean equals(Object obj);
	}
	
	protected abstract T getCanonical(T obj);
	
	protected abstract void writeObject(T obj, PacketBufferCC buffer);
	
	protected abstract T readObject(PacketBufferCC buffer) throws IOException;
	
	protected String getCacheName()
	{
		return getClass().getSimpleName();
	}
	
	private I serverStore(T object)
	{
		T canonical = getCanonical(object);
		I current = serverObjectToID.get(canonical);
		
		if(current == null)
		{
			I id = nextId(object);
			serverObjectToID.forcePut(canonical, id);
			return id;
		}
		
		else {return current;}
	}
	
	@SuppressWarnings("unchecked")
	private I serverGetID(Object obj)
	{
		if(obj.getClass() != defaultObject.getClass()) {return null;}
		
		else
		{
			T object = (T) obj;
			T canonical = getCanonical(object);
			I current = serverObjectToID.get(canonical);
			
			if(current == null) {return null;}
			
			else {return current;}
		}
	}
	
	protected abstract Link clientRetrieve(I id);
	
	@SuppressWarnings("unchecked")
	void writeObjectServer(Object id, PacketBufferCC buffer)
	{
		try
		{
			T obj = serverIDToObject.get((I) id);
			writeObject(obj, buffer);
		}
		
		catch(ClassCastException e)
		{
			CCLog.logger.error("Attempted to writeObjectServer with incompatible id Object");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void readObjectClient(Object id, PacketBufferCC buffer) throws IOException
	{
		try
		{
			Link link = clientRetrieve((I) id);
			link.actual = readObject(buffer);
		}
		
		catch(ClassCastException e)
		{
			CCLog.logger.error("Attempted to readObjectClient with incompatible id Object");
		}
	}
	
	public abstract MessageObjectCacheResponse<I> getResponse(Object[] ids, byte[][] values);
	
	final String getNameAndID()
	{
		return getCacheName();
	}
	
	public abstract void onClientWorldTick();
	
	void onClientJoinServer()
	{
		clientObjects.clear();
		clientUnknowns.clear();
	}
	
	public abstract I nextId(T object);
}
