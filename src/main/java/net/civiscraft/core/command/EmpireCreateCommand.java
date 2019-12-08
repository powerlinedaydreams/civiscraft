package net.civiscraft.core.command;

import java.util.UUID;

import net.civiscraft.core.CCCore;
import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.core.empire.ClientEmpire;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.core.net.PlayerEmpireAddMessage;
import net.civiscraft.lib.command.CivisCommandBase;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class EmpireCreateCommand extends CivisCommandBase
{
	@Override
	public String getName()
	{
		return "create";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/cc empire create <playerid>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();

		if(args.length > 0)
		{
			player = (EntityPlayerMP) server.getWorld(0).getPlayerEntityByName(args[1]);

			if(player == null)
			{
				sender.sendMessage(
						new TextComponentString(TextFormatting.RED + "Empire creation failed.  Invalid player name."));
				return;
			}
		}

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
			playerEmpire.setName("empireName");
			playerEmpire.addPlayer(player.getUniqueID());
			playerEmpire.addTerritory(tilePos);
			intel.setPlayerEmpire(playerEmpire.id);
			CCCore.NETWORK_CHANNEL.sendTo(new PlayerEmpireAddMessage(new ClientEmpire(playerEmpire, player)), player);
		}

		else
		{
			ITextComponent message = new TextComponentString(TextFormatting.RED + "Empire creation failed. "
					+ player.getName() + " already belongs to an empire.");
			sender.sendMessage(message);
			return;
		}
	}

	@Override
	public String getDescription()
	{
		return "Creates new empire";
	}

	@Override
	public String[] getThisUsage(ICommandSender sender)
	{
		String[] usage = { "... create <playerid>" };

		return usage;
	}

	@Override
	public String getTitle()
	{
		return "Create";
	}

}
