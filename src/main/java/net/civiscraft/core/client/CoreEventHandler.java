package net.civiscraft.core.client;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.civiscraft.core.CCCore;
import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.core.client.gui.EmpireInfoGui;
import net.civiscraft.core.client.gui.VanillaGui;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.core.event.EmpireEvent;
import net.civiscraft.core.net.EmpireRequestMessage;
import net.civiscraft.core.net.TileClaimMessage;
import net.civiscraft.core.proxy.ClientProxy;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.util.CivisMathUtil;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.map.tile.Tile;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class CoreEventHandler
{
	private static float topBarHold = 0;
	private static boolean leftLock = false;
	private static boolean rightLock = false;
	private static boolean empireCheck = false;
	private static VanillaGui vanillaGui = null;
	private static PrimitiveTileMarker tileMarker = new PrimitiveTileMarker(Minecraft.getMinecraft());

	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent e)
	{
		tileMarker.render(e.getPartialTicks(), 10L);
	}

	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre e)
	{

		return;
		/*
		if(e.getType() == ElementType.ALL)
		{
			vanillaGui = new VanillaGui(Minecraft.getMinecraft());
			return;
		}
		
		e.setCanceled(true);
		
		switch (e.getType())
			{
			case HOTBAR:
				vanillaGui.renderHotbar(e.getResolution(), e.getPartialTicks());
			default:
				break;
			}*/
	}

	@SubscribeEvent
	public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post e)
	{
		if(!empireCheck)
		{
			CCCore.NETWORK_CHANNEL.sendToServer(new EmpireRequestMessage(false));
			empireCheck = true;
		}

		if(e.getType() != ElementType.ALL) return;

		if(ClientProxy.KEY_TOP_BAR_LEFT.isKeyDown() && !rightLock)
		{
			leftLock = true;
			topBarHold += e.getPartialTicks();
			EmpireInfoGui.moveTopBarLeft(1 + 6 * CivisMathUtil.smoothStop4(topBarHold / 500));
		}

		else if(leftLock)
		{
			leftLock = false;
		}

		if(ClientProxy.KEY_TOP_BAR_RIGHT.isKeyDown() && !leftLock)
		{
			rightLock = true;
			topBarHold += e.getPartialTicks();
			EmpireInfoGui.moveTopBarRight(1 + 6 * CivisMathUtil.smoothStop4(topBarHold / 500));
		}

		else if(rightLock)
		{
			rightLock = false;
		}

		if(topBarHold != 0 && !leftLock && !rightLock)
		{
			topBarHold = 0;
		}

		new EmpireInfoGui(Minecraft.getMinecraft());
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END)
		{
			if(ClientProxy.KEY_CREATE_EMPIRE.isPressed())
			{
				EntityPlayer player = Minecraft.getMinecraft().player;
				player.sendMessage(new TextComponentString("Create Empire Key Pressed"));
				if(ClientDisplayData.getData().playerEmpire == Empire.NULL)
				{
					CCCore.NETWORK_CHANNEL.sendToServer(new EmpireRequestMessage(true));
				}
			}

			if(ClientProxy.KEY_CLAIM_TILE.isPressed())
			{
				boolean failed = false;
				Minecraft minecraft = Minecraft.getMinecraft();
				EntityPlayer player = minecraft.player;
				player.sendMessage(new TextComponentString("Claim Tile Key Pressed"));

				if(ClientDisplayData.getData().playerEmpire == Empire.NULL)
				{
					player.sendMessage(new TextComponentString("No active empire. Tile claim failed."));
					failed = true;
				}

				TilePos pos = new TilePos(new ChunkPos(player.chunkCoordX, player.chunkCoordZ));
				ClientDisplayData data = ClientDisplayData.getData();
				ClientTile tile = data.tiles.get(pos);

				if(tile == null)
				{
					player.sendMessage(new TextComponentString("Tile is not in ClientDisplayData. Tile claim failed."));
					failed = true;
				}

				else if(tile.owner.isOwned)
				{
					if(tile.owner.getOwner().equals(data.playerEmpire))
					{
						player.sendMessage(new TextComponentString("You already own this tile. Tile claim failed."));
						failed = true;
					}

					else
					{
						player.sendMessage(new TextComponentString("Tile is already owned by "
								+ data.empires.get(tile.owner.getOwner()).name + ". Tile claim failed."));
						failed = true;
					}
				}

				if(!failed)
				{
					player.sendMessage(new TextComponentString("Claim Message Sent"));
					CCCore.NETWORK_CHANNEL.sendToServer(new TileClaimMessage(pos));
				}
			}
		}

		//check to see if player already has an empire
		//if not, send a message to server to request that one be created
		//have server check to see if the player already has an empire
		//if not, create a new empire based on the player's current tile
		//send a message back to the client updating it with the player's new empire
		//display that information in the HUD
		// ???
		// profit.
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.LoadFromFile e)
	{
		ClientDisplayData.refreshData();
	}

	@SubscribeEvent
	public void onEmpireDelete(EmpireEvent.Delete e)
	{
		Empire empire = e.getEmpire();
		World world = e.getWorld();

		Map<TilePos, Integer> tiles = empire.getTiles();
		TileList tileList = TileList.get(world);

		for (Entry<TilePos, Integer> entry : tiles.entrySet())
		{
			Tile tile = tileList.getTileByPos(entry.getKey());
			tile.getOwner().removeEmpire(empire.id);
		}

		tiles.clear();

		CCLog.logger.info(empire.getTiles().size());

		for (UUID playerID : empire.getPlayers())
		{
			EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(playerID);
			PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);
			intel.setPlayerEmpire(Empire.NULL);
		}
	}
}
