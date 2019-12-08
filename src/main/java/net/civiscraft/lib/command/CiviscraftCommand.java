package net.civiscraft.lib.command;

import net.civiscraft.core.command.EmpireCommand;
import net.minecraft.command.ICommandSender;

public class CiviscraftCommand extends CivisCommandTreeBase
{
	public CiviscraftCommand()
	{
		addSubcommand(new EmpireCommand());
		addSubcommand(new HelpCommand());
	}

	@Override
	public String getName()
	{
		return "cc";
	}

	@Override
	public String[] getThisUsage(ICommandSender sender)
	{
		String[] usage = { "cc <arguments>" };

		return usage;
	}

	@Override
	public String getTitle()
	{
		return "Civiscraft";
	}
}
