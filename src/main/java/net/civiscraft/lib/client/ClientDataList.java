package net.civiscraft.lib.client;

import java.util.HashMap;

import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.net.cache.CCClientObjectCaches;
import net.civiscraft.lib.net.cache.CCClientObjectCaches.CacheType;
import net.civiscraft.lib.net.cache.ClientObjectCache;
import net.civiscraft.lib.util.data.ICacheable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ClientDataList<T extends CCClientObject, C extends ClientObjectCache<T, I>, I extends ICacheable<I>>
{	
	public static int index = -1;
	public final CacheType type;
	
	protected final T defaultObject;
	
	private final HashMap<I, C.Link> data = new HashMap<I, C.Link>();
	
	public ClientDataList(T defaultObject, CacheType type) 
	{
		this.defaultObject = defaultObject;
		this.type = type;
	}
	
	public void registered(int i)
	{
		index = i;
	}
	
	@SuppressWarnings("unchecked")
	public C.Link getItem(I id)
	{
		C.Link item = data.get(id);
		
		if(item == null) {return null;}
		
		T obj = item.get();
		
		if(obj.isChanged)
		{
			C.ClientView client = ((C) CCClientObjectCaches.get(type)).client();
			item = client.retrieve(item.id);
			return item;
		}
		
		else {return item;}
	}
	
	public void addItem(I id, C.Link item)
	{
		data.put(id, item);
		CCLog.logger.info(item == null ? "Null item added in " + toString() : "Item added in " + toString()); //////////////////////////////////////////////
	}
	
	public boolean containsId(I id)
	{
		return data.containsKey(id);
	}
	
	public void onClientJoinsServer()
	{
		data.clear();
	}
}
