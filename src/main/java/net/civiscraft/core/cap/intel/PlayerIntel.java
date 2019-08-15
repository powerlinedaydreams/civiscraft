package net.civiscraft.core.cap.intel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import javax.annotation.Nullable;

import net.civiscraft.core.empire.Empire;
import net.civiscraft.lib.cap.intel.IIntel;
import net.civiscraft.world.event.TileWatchEvent;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.resource.Resource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerIntel implements IIntel
{
	EntityPlayer player;
	private UUID playerEmpire = Empire.nullEmpire;
	private ArrayList<UUID> empires = new ArrayList<UUID>();
	private ArrayList<Resource> resources = new ArrayList<Resource>();
	private HashSet<TilePos> watchedTiles = new HashSet<TilePos>();
	private HashSet<TilePos> seenTiles = new HashSet<TilePos>();

	public PlayerIntel(@Nullable EntityPlayer player)
	{
		this.player = player;
	}

	public UUID getPlayerEmpire()
	{
		return playerEmpire;
	}

	public void setPlayerEmpire(UUID empireId)
	{
		playerEmpire = empireId;
		empires.add(empireId);
	}

	@Override
	public void addEmpire(UUID empireID)
	{
		if(!empires.contains(empireID))
		{
			empires.add(empireID);
		}
	}

	@Override
	public void addEmpires(ArrayList<UUID> empireList)
	{
		for (UUID empireID : empireList)
		{
			if(!empires.contains(empireID))
			{
				empires.add(empireID);
			}
		}
	}

	@Override
	public void addResource(Resource resource)
	{
		if(!resources.contains(resource))
		{
			resources.add(resource);
		}
	}

	@Override
	public void addResources(ArrayList<Resource> resourceList)
	{
		for (Resource resource : resourceList)
		{
			if(!resources.contains(resource))
			{
				resources.add(resource);
			}
		}
	}

	@Override
	public ArrayList<UUID> getEmpires()
	{
		return empires;
	}

	@Override
	public ArrayList<Resource> getResources()
	{
		return resources;
	}

	@Override
	public void set(IIntel intel)
	{
		PlayerIntel playerIntel = (PlayerIntel) intel;
		this.player = playerIntel.getPlayer();
		this.empires = playerIntel.getEmpires();
		this.resources = playerIntel.getResources();
	}

	private EntityPlayer getPlayer()
	{
		return player;
	}

	@Override
	public void set(ArrayList<UUID> empires, ArrayList<Resource> resources)
	{
		this.empires = empires;
		this.resources = resources;
	}

	@Override
	public void addWatchedTile(TilePos tile)
	{
		boolean isNew = watchedTiles.add(tile);
		if(!player.world.isRemote && isNew)
		{
			net.minecraftforge.common.MinecraftForge.EVENT_BUS
					.post(new TileWatchEvent.Watch(tile, (EntityPlayerMP) player));

			if(!seenTiles.contains(tile))
			{
				net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new TileWatchEvent.Seen(tile, (EntityPlayerMP) player));
			}
		}
	}

	@Override
	public void addWatchedTiles(ArrayList<TilePos> tiles)
	{
		@SuppressWarnings("unchecked")
		ArrayList<TilePos> wTiles = (ArrayList<TilePos>) tiles.clone();
		@SuppressWarnings("unchecked")
		ArrayList<TilePos> sTiles = (ArrayList<TilePos>) tiles.clone();

		for (TilePos tile : tiles)
		{
			boolean wIsNew = watchedTiles.add(tile);

			if(wIsNew)
			{
				boolean sIsNew = seenTiles.add(tile);

				if(!sIsNew)
				{
					sTiles.remove(tile);
				}
			}

			else
			{
				wTiles.remove(tile);
			}
		}

		if(!player.world.isRemote && wTiles.size() > 0)
		{
			if(wTiles.size() == 1)
			{
				net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new TileWatchEvent.Watch(wTiles.get(0), (EntityPlayerMP) player));
			}

			else
			{
				net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new TileWatchEvent.Watch(wTiles, (EntityPlayerMP) player));
			}
		}

		if(!player.world.isRemote && sTiles.size() > 0)
		{
			if(sTiles.size() == 1)
			{
				net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new TileWatchEvent.Seen(sTiles.get(0), (EntityPlayerMP) player));
			}

			else
			{
				net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new TileWatchEvent.Seen(sTiles, (EntityPlayerMP) player));
			}
		}
	}

	@Override
	public ArrayList<TilePos> getWatchedTiles()
	{
		ArrayList<TilePos> tileArray = new ArrayList<TilePos>();
		tileArray.addAll(watchedTiles);
		return tileArray;
	}

	@Override
	public ArrayList<TilePos> getSeenTiles()
	{
		ArrayList<TilePos> tileArray = new ArrayList<TilePos>();
		tileArray.addAll(watchedTiles);
		return tileArray;
	}

	@Override
	public void removeWatchedTile(TilePos tile)
	{
		boolean wasThere = watchedTiles.remove(tile);

		if(!player.world.isRemote && wasThere)
		{
			net.minecraftforge.common.MinecraftForge.EVENT_BUS
					.post(new TileWatchEvent.UnWatch(tile, (EntityPlayerMP) player));
		}
	}

	@Override
	public void removeWatchedTiles(ArrayList<TilePos> tiles)
	{
		@SuppressWarnings("unchecked")
		ArrayList<TilePos> _tiles = (ArrayList<TilePos>) tiles.clone();

		for (TilePos tile : tiles)
		{
			boolean wasThere = watchedTiles.remove(tile);

			if(!wasThere)
			{
				_tiles.remove(tile);
			}
		}

		if(!player.world.isRemote && _tiles.size() > 0)
		{
			if(_tiles.size() == 1)
			{
				net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new TileWatchEvent.UnWatch(_tiles.get(0), (EntityPlayerMP) player));
			}

			else
			{
				net.minecraftforge.common.MinecraftForge.EVENT_BUS
						.post(new TileWatchEvent.UnWatch(_tiles, (EntityPlayerMP) player));
			}
		}
	}
}
