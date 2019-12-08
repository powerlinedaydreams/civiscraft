package net.civiscraft.core.command;

import java.util.UUID;

import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.lib.command.CivisCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class EmpireEchoCommand extends CivisCommandBase
{

	@Override
	public String getName()
	{
		return "echo";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();

		ITextComponent message = new TextComponentString(TextFormatting.GRAY + "You have no current empire");

		if(args.length > 1)
		{
			player = (EntityPlayerMP) server.getWorld(0).getPlayerEntityByName(args[1]);

			if(player == null)
			{
				sender.sendMessage(
						new TextComponentString(TextFormatting.RED + "Empire lookup failed.  Invalid player name."));
				return;
			}

			message = new TextComponentString(TextFormatting.GRAY + player.getName() + " has no current empire.");
		}

		PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);
		UUID empire = intel.getPlayerEmpire();

		if(empire != null && !empire.equals(Empire.NULL))
		{
			message = new TextComponentString(TextFormatting.YELLOW + empire.toString());
		}

		sender.sendMessage(message);

		return;
	}

	@Override
	public String getDescription()
	{
		return "Replies with your empire";
	}

	@Override
	public String[] getThisUsage(ICommandSender sender)
	{
		String[] usage = { "... echo" };

		return usage;
	}

	@Override
	public String getTitle()
	{
		return "Echo";
	}

}
