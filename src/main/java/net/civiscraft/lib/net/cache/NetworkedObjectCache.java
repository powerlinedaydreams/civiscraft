package net.civiscraft.lib.net.cache;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.civiscraft.lib.net.MessageManager;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.net.cache.CCClientObjectCaches.CacheType;
import net.civiscraft.lib.util.data.ICacheable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class NetworkedObjectCache<I extends ICacheable<I>, T>
{
	public static String name;
	
	protected final T defaultObject;
	
	protected final BiMap<I, T> serverIDToObject = HashBiMap.create();
	protected final BiMap<T, I> serverObjectToID = serverIDToObject.inverse();
	
	private I serverCurrentID;
	
	protected final Int2ObjectMap<Link> clientObjects = new Int2ObjectOpenHashMap<>();
	protected final Queue<Link> clientUnknowns = new LinkedList<>();
	
	private final ServerView serverView = new ServerView();
	private final ClientView clientView = new ClientView();
	
	public NetworkedObjectCache(T defaultObject, String name)
	{
		this.defaultObject = defaultObject;
		this.name = name;
	}
	
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
	
	public class ServerView
	{
		private ServerView() {}
		
		public int store(T value)
		{
			return serverStore(value);
		}
		
		public int getID(T value)
		{
			return serverGetID(value);
		}

		public void processDataRequest(PacketBufferCC buffer, EntityPlayerMP player)
		{
			
		}
	}
	
	public class ClientView
	{
		private ClientView() {}
		
		public Link retrieve(int id)
		{
			return clientRetrieve(id);
		}
		
		public void update(int id)
		{
			clientUpdate(id);
		}
	}
	
	public class Link implements Supplier<T>
	{
		public T actual;
		
		public final int id;
		
		Link(int id)
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
		public boolean equals(Object obj)
		{
			if(obj.getClass() == int.class) {return (int) obj == id;}
				
			return actual.equals(obj);
		}
	}
	
	protected abstract T getCanonical(T obj);
	
	protected abstract void writeObject(T obj, PacketBufferCC buffer);
	
	protected abstract T readObject(PacketBufferCC buffer) throws IOException;
	
	protected abstract CacheType getCacheType();
	
	protected String getCacheName()
	{
		return getClass().getSimpleName();
	}
	
	private int serverStore(T object)
	{
		T canonical = getCanonical(object);
		I current = serverObjectToID.get(canonical);
		
		if(current == null)
		{
			int id = serverCurrentID++;
			serverObjectToID.put(canonical, id);
			return id;
		}
		
		else {return current;}
	}
	
	private int serverGetID(Object obj)
	{
		if(obj.getClass() != defaultObject.getClass()) {return -1;}
		
		else
		{
			//It is checked, it just doesn't know it
			@SuppressWarnings("unchecked")
			T object = (T) obj;
			T canonical = getCanonical(object);
			Integer current = serverObjectToID.get(canonical);
			
			if(current == null) {return -1;}
			
			else {return current;}
		}
	}
	
	private Link clientRetrieve(int id)
	{
		Link current = clientObjects.get(id);
		if (current == null)
		{
			current = new Link(id);
			clientUnknowns.add(current);
			clientObjects.put(id, current);
		}
		
		return current;
	}
	
	private void clientUpdate(int id)
	{
		Link current = clientObjects.get(id);
		if (current == null)
		{
			current = new Link(id);
			clientUnknowns.add(current);
			clientObjects.put(id, current);
		}
		
		else
		{
			clientUnknowns.add(current);
		}
	}
	
	void writeObjectServer(int id, PacketBufferCC buffer)
	{
		T obj = serverIDToObject.get(id);
		writeObject(obj, buffer);
	}
	
	void readObjectClient(int id, PacketBufferCC buffer) throws IOException
	{
		Link link = clientRetrieve(id);
		link.actual = readObject(buffer);
	}
	
	final String getNameAndID()
	{
		return getCacheName();
	}
	
	void onClientWorldTick()
	{
		int[] ids = new int[clientUnknowns.size()];
		for(int i=0; i<ids.length; i++)
		{
			ids[i] = clientUnknowns.remove().id;
		}
		
		if(ids.length > 0)
		{
			MessageManager.sendToServer(new MessageObjectCacheRequest(ids, getCacheType()));
		}
	}
	
	void onClientJoinServer()
	{
		clientObjects.clear();
		clientUnknowns.clear();
	} 
}
