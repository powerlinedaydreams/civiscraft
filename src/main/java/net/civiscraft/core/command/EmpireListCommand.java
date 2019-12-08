package net.civiscraft.core.command;

import java.util.Map.Entry;
import java.util.UUID;

import net.civiscraft.core.empire.Empire;
import net.civiscraft.core.worldsaveddata.EmpireList;
import net.civiscraft.lib.command.CivisCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class EmpireListCommand extends CivisCommandBase
{
	@Override
	public String getName()
	{
		return "list";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EmpireList empires = EmpireList.get(server.getWorld(0));

		String message = TextFormatting.BOLD + "Empires: \n" + TextFormatting.RESET;

		for (Entry<UUID, Empire> empire : empires.getEmpires())
		{
			message += " - " + empire.toString() + "\n";
		}

		sender.sendMessage(new TextComponentString(message));
		return;
	}

	@Override
	public String getDescription()
	{
		return "Lists extant empires";
	}

	@Override
	public String[] getThisUsage(ICommandSender sender)
	{
		String[] usage = { "... list" };

		return usage;
	}

	@Override
	public String getTitle()
	{
		return "List";
	}

}
