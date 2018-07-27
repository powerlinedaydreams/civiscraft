package net.civiscraft.lib.chunkload;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class ChunkLoader implements ForgeChunkManager.OrderedLoadingCallback
{
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		
	}
	
	@Override
	public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount)
	{
		return new ArrayList<Ticket>();
	}
}
