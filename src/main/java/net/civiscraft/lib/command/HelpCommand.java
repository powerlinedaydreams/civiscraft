package net.civiscraft.lib.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class HelpCommand extends CivisCommandBase
{

	@Override
	public String getName()
	{
		return "help";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{

	}

	@Override
	public String getDescription()
	{
		return "Provides help text for command";
	}

	@Override
	public String[] getThisUsage(ICommandSender sender)
	{
		String[] usage = { "... help <command path>" };

		return usage;
	}

	@Override
	public String getTitle()
	{
		return "Help";
	}

}
