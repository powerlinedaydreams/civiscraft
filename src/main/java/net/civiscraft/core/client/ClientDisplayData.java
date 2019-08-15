package net.civiscraft.core.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.civiscraft.core.empire.ClientEmpire;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientDisplayData
{
	private static ClientDisplayData INSTANCE;
	public Map<TilePos, ClientTile> tiles = new HashMap<TilePos, ClientTile>();
	public Map<UUID, ClientEmpire> empires = new HashMap<UUID, ClientEmpire>();
	@Nullable
	public UUID playerEmpire = Empire.nullEmpire;

	public static ClientDisplayData getData()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new ClientDisplayData();
		}

		return INSTANCE;
	}

	public static void refreshData()
	{
		INSTANCE = new ClientDisplayData();
	}
}
