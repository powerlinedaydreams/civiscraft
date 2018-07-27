package net.civiscraft.lib.cap.intel;

import java.util.ArrayList;
import java.util.UUID;

import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.resource.Resource;

public interface IIntel
{
	public void addEmpire(UUID empireID);
	
	public void addEmpires(ArrayList<UUID> empires);
	
	public void addResource(Resource resource);
	
	public void addResources(ArrayList<Resource> resources);
	
	public void addWatchedTile(TilePos tile);
	
	public void addWatchedTiles(ArrayList<TilePos> tiles);
	
	public ArrayList<UUID> getEmpires();
	
	public ArrayList<Resource> getResources();
	
	public ArrayList<TilePos> getWatchedTiles();
	
	public ArrayList<TilePos> getSeenTiles();
	
	public void removeWatchedTile(TilePos tile);
	
	public void removeWatchedTiles(ArrayList<TilePos> tiles);
	
	public void set(IIntel intel);
	
	public void set(ArrayList<UUID> empires, ArrayList<Resource> resources);
}
