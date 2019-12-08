package net.civiscraft.core.command;

import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.core.worldsaveddata.EmpireList;
import net.civiscraft.lib.command.CivisCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class EmpireDeleteCommand extends CivisCommandBase
{

	@Override
	public String getName()
	{
		return "delete";
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
			sender.sendMessage(new TextComponentString(
					TextFormatting.RED + "Empire deletion failed.  Player does not belong to an empire."));
		}

		else
		{
			Empire empire = EmpireList.get(server.getWorld(0)).getEmpire(intel.getPlayerEmpire());
			empire.delete(server.getWorld(0));
		}

	}

	@Override
	public String getDescription()
	{
		return "Deletes empire";
	}

	@Override
	public String[] getThisUsage(ICommandSender sender)
	{
		String[] usage = { "... delete", "... delete <empire id>" };

		return usage;
	}

	@Override
	public String getTitle()
	{
		return "Delete";
	}

}
