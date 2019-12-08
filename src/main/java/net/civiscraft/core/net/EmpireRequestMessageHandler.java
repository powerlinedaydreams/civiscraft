package net.civiscraft.core.net;

import java.util.UUID;

import net.civiscraft.core.CCCore;
import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.core.empire.ClientEmpire;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.core.worldsaveddata.EmpireList;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.world.map.tile.TilePos;
import net.civiscraft.world.worldsaveddata.TileList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EmpireRequestMessageHandler implements IMessageHandler<EmpireRequestMessage, IMessage>
{
	@Override
	public IMessage onMessage(EmpireRequestMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		CCLog.logger.info("Empire requested");

		if(message.isNewEmpire)
		{
			player.world.getMinecraftServer().addScheduledTask(() ->
				{
					PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);
					if(intel.getPlayerEmpire().equals(Empire.NULL))
					{
						TilePos tilePos = new TilePos(new ChunkPos(player.chunkCoordX, player.chunkCoordZ));
						UUID id = UUID.randomUUID();
						while (id == Empire.NULL)
						{
							id = UUID.randomUUID();
						}
						Empire playerEmpire = new Empire(id, player.world);
						intel.setPlayerEmpire(playerEmpire.id);
						CCCore.NETWORK_CHANNEL
								.sendTo(new PlayerEmpireAddMessage(new ClientEmpire(playerEmpire, player)), player);
					}
				});
			return null;
		}

		player.world.getMinecraftServer().addScheduledTask(() ->
			{
				PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);
				if(!intel.getPlayerEmpire().equals(Empire.NULL))
				{
					CCLog.logger.info(intel.getPlayerEmpire());
					CCLog.logger.info(
							EmpireList.get(ctx.getServerHandler().player.world).getEmpire(intel.getPlayerEmpire()));
					TileList tileList = TileList.get(ctx.getServerHandler().player.world);
					CCLog.logger.info(tileList.getTileByPos(new TilePos(0, 0)));
					CCCore.NETWORK_CHANNEL.sendTo(new PlayerEmpireAddMessage(new ClientEmpire(
							EmpireList.get(ctx.getServerHandler().player.world).getEmpire(intel.getPlayerEmpire()),
							player)), player);
				}
			});
		return null;
	}
}
