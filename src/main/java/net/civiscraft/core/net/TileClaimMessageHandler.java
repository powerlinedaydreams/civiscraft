package net.civiscraft.core.net;

import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.core.worldsaveddata.EmpireList;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.world.CCWorld;
import net.civiscraft.world.client.tile.ClientTile;
import net.civiscraft.world.map.tile.Tile;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.net.TileAddMessage;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TileClaimMessageHandler implements IMessageHandler<TileClaimMessage, IMessage>
{
	@Override
	public IMessage onMessage(TileClaimMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		player.world.getMinecraftServer().addScheduledTask(new Runnable()
		{
			@Override
			public void run()
			{
				boolean failed = false;
				TileList tileList = TileList.get(player.world);
				EmpireList empireList = EmpireList.get(player.world);
				PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);

				TilePos pos = message.tilePos;
				Tile tile = tileList.getTileByPos(pos);
				if(tile == null)
				{
					player.sendMessage(new TextComponentString("Tile is not in ClientDisplayData. Tile claim failed."));
					failed = true;
				}

				if(tile.getOwner().isOwned)
				{
					if(tile.getOwner().getOwner().equals(intel.getPlayerEmpire()))
					{
						player.sendMessage(new TextComponentString("You already own this tile. Tile claim failed."));
						failed = true;
					}

					else
					{
						player.sendMessage(new TextComponentString(
								"Tile is already owned by " + (intel.getEmpires().contains(tile.getOwner().getOwner())
										? empireList.getEmpire(tile.getOwner().getOwner()).name
										: "an unknown empire") + ". Tile claim failed."));
						failed = true;
					}
				}

				if(!failed)
				{
					player.sendMessage(new TextComponentString("Message received"));
					tile.getOwner().updateInfluence(intel.getPlayerEmpire(), 100);
					CCLog.logger.info(tileList.getTileByPos(pos).getOwner().isOwned);
					tileList.tileUpdated();
					CCWorld.NETWORK_CHANNEL.sendTo(new TileAddMessage(new ClientTile(tile, player)), player);
				}
			}
		});

		return null;
	}
}
