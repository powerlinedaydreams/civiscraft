package net.civiscraft.lib.event;

import net.civiscraft.lib.CCLibProxy;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LibEventHandler
{
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e)
	{
		World world = e.getWorld();
		if(!world.isRemote)
		{
			TileList.get(world);
		}
	}
	
	@SubscribeEvent
	public void onRenderLast(RenderWorldLastEvent e)
	{
		float partialTicks = e.getPartialTicks();
		CCLibProxy.CHROME_RENDERER.renderChrome(partialTicks);
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e)
	{
		//CCClientObjectCaches.onClientTick();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientJoinServer(ClientConnectedToServerEvent e)
	{
		CCLog.logger.info("ClientLoggedIn");
		//CCClientObjectCaches.onClientJoinServer();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientLeaveServer(ClientDisconnectionFromServerEvent e)
	{
		//CCClientObjectCaches.onClientLeaveServer();
	}
}
