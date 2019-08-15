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

public abstract class NetworkedObjectCache<T> {

    /* Implementation notes -- this currently is a simple, never expiring object<->id cache.
     * 
     * Because it doesn't ever clear objects out of the cache we can guarantee that the index of an object is unique,
     * just by incrementing a single variable. */

    /** The default object -- used at the client in case the object hasn't been sent to the client yet. */
    protected final T defaultObject;

    private final BiMap<Integer, T> serverIdToObject = HashBiMap.create();
    /** Server side map of the object to its integer ID. Inverse of {@link #serverIdToObject} */
    private final BiMap<T, Integer> serverObjectToId = serverIdToObject.inverse();

    /** The ID for the next stored object. */
    private int serverCurrentId = 0;

    /** The list of cached client-side objects. */
    private final Int2ObjectMap<Link> clientObjects = new Int2ObjectOpenHashMap<>();
    /** The list of all links that are currently unknown. */
    private final Queue<Link> clientUnknowns = new LinkedList<>();

    /** A server view of this cache. Contains methods specific to */
    private final ServerView serverView = new ServerView();
    private final ClientView clientView = new ClientView();

    public NetworkedObjectCache(T defaultObject) {
        this.defaultObject = defaultObject;
    }

    // Public API

    /** @return The server view of this cache. If the debug option "lib.net.cache" is enabled then this will check to
     *         make sure that this really is the server thread. */
    public ServerView server() {
        return serverView;
    }

    /** @return The client view of this cache. If the debug option "lib.net.cache" is enabled then this will check to
     *         make sure that this really is the client thread. */
    public ClientView client() {
        return clientView;
    }

    /** The server view of the cache. */
    public class ServerView {
        private ServerView() {}

        /** Stores the given object in this cache, returning its ID.
         * 
         * @param value The object to store
         * @return The id that maps back to the canonicalised version of the value. */
        public int store(T value) {
            return serverStore(value);
        }

        /** Gets the ID for the given object, or -1 if this was not stored in the cache. {@link #store(Object)} is
         * preferred to this, as most uses (such as network sending) want the value to be stored and get a valid ID.
         * 
         * @param value The value to get an id for
         * @return */
        public int getId(T value) {
            return serverGetId(value);
        }
    }

    /** The client view of the cache. */
    public class ClientView {
        private ClientView() {}

        /** @param id The id of the given object.
         * @return A link to the stored object. The returned link should be stored (only 1 instance exists per stored
         *         integer ID) in preference to calling this method, as then you can avoid the map lookup. Th returned
         *         link object is updated if */
        public Link retrieve(int id) {
            return clientRetrieve(id);
        }
    }

    /** Defines a link to a cached object (on the client - don't use this on the server). If */
    public class Link implements Supplier<T> {

        /** The stored, cached value. */
        T actual;

        /** The id of this value. */
        final int id;

        Link(int id) {
            this.id = id;
        }

        @Override
        public T get() {
            return actual == null ? defaultObject : actual;
        }

        public boolean hasBeenReceived() {
            return actual != null;
        }
    }

    // Abstract overridable methods

    /** Takes a specific object and turns it into its most basic form. For example for {@link ItemStack}'s this will
     * should set the stack size to 1, and remove all non-rendered NBT tag components.
     * 
     * @param obj The object to canonicalized.
     * @return A canonical version of the input */
    protected abstract T getCanonical(T obj);

    /** Writes the specified object out to the buffer.
     * 
     * @param obj The object to write. It will have already been passed through {@link #getCanonical(Object)}
     * @param buffer The buffer to write into. */
    protected abstract void writeObject(T obj, PacketBufferBC buffer);

    /** Reads the specified object from the buffer. Note that the returned object should be identity equal to itself
     * passed into {@link #getCanonical(Object)} (so {@code  value.equals(getCanonical(value)) } should return true.)
     * 
     * @param buffer The buffer to read from
     * @return */
    protected abstract T readObject(PacketBufferBC buffer) throws IOException;

    /** @return The name of this cache to be used in debug messages. */
    protected String getCacheName() {
        return getClass().getSimpleName();
    }

    // Internal logic

    /** Stores the given object in this cache, returning its ID. SERVER SIDE.
     * 
     * @param object
     * @return */
    private int serverStore(T object) {
        T canonical = getCanonical(object);
        Integer current = serverObjectToId.get(canonical);
        if (current == null) {
            // new entry
            int id = serverCurrentId++;
            serverObjectToId.put(canonical, id);
            if (DEBUG_CPLX) {
                String toString;
                if (canonical instanceof FluidStack) {
                    FluidStack fluid = (FluidStack) canonical;
                    toString = fluid.getUnlocalizedName();
                } else {
                    toString = canonical.toString();
                }
                BCLog.logger.info("[lib.net.cache] The cache " + getNameAndId() + " stored #" + id + " as " + toString);
            }
            return id;
        } else {
            // existing entry
            return current;
        }
    }

    /** Gets the ID for the given object, or -1 if this was not stored in the cache. SERVER SIDE.
     * {@link #serverStore(Object)} if preferred to this, as most uses (such as network sending) want the value to be
     * stored and get a valid ID.
     * 
     * @param object
     * @return */
    private int serverGetId(T object) {
        T canonical = getCanonical(object);
        Integer current = serverObjectToId.get(canonical);
        if (current == null) {
            // Unknown entry
            return -1;
        } else {
            return current;
        }
    }

    /** Retrieves a link to the specified ID. CLIENT SIDE.
     * 
     * @param id
     * @return */
    private Link clientRetrieve(int id) {
        Link current = clientObjects.get(id);
        if (current == null) {
            if (DEBUG_CPLX) {
                BCLog.logger.info("[lib.net.cache] The cache " + getNameAndId() + " tried to retrieve #" + id + " for the first time");
            }
            current = new Link(id);
            clientUnknowns.add(current);
            clientObjects.put(id, current);
        }
        return current;
    }

    /** Used by {@link MessageObjectCacheRequest#HANDLER} to write the actual object out. */
    void writeObjectServer(int id, PacketBufferBC buffer) {
        T obj = serverIdToObject.get(id);
        writeObject(obj, buffer);
    }

    /** Used by {@link MessageObjectCacheResponse#HANDLER} to read an object in.
     * 
     * @param id
     * @param buffer
     * @throws IOException */
    void readObjectClient(int id, PacketBufferBC buffer) throws IOException {
        Link link = clientRetrieve(id);
        link.actual = readObject(buffer);
        if (DEBUG_CPLX) {
            T read = link.actual;
            String toString;
            if (read instanceof FluidStack) {
                FluidStack fluid = (FluidStack) read;
                toString = fluid.getUnlocalizedName();
            } else {
                toString = read.toString();
            }
            BCLog.logger.info("[lib.net.cache] The cache " + getNameAndId() + " just received #" + id + " as " + toString);
        }
    }

    final String getNameAndId() {
        return "(" + BuildCraftObjectCaches.CACHES.indexOf(this) + " = " + getCacheName() + ")";
    }

    void onClientWorldTick() {
        int[] ids = new int[clientUnknowns.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = clientUnknowns.remove().id;
        }
        if (ids.length > 0) {
            if (DEBUG_CPLX) {
                BCLog.logger.info("[lib.net.cache] The cache " + getNameAndId() + " requests ID's " + Arrays.toString(ids));
            }
            MessageManager.sendToServer(new MessageObjectCacheRequest(this, ids));
        }
    }

    void onClientJoinServer() {
        clientObjects.clear();
        clientUnknowns.clear();
    }
}

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
		
		public final I id;
		
		Link(I id)
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
			if(obj.getClass() == id.getClass()) {return (I) obj == id;}
				
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
