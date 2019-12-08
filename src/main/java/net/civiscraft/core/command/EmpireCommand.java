package net.civiscraft.core.command;

import net.civiscraft.lib.command.CivisCommandTreeBase;
import net.minecraft.command.ICommandSender;

public class EmpireCommand extends CivisCommandTreeBase
{
	public EmpireCommand()
	{
		addSubcommand(new EmpireCreateCommand());
		addSubcommand(new EmpireDeleteCommand());
		addSubcommand(new EmpireListCommand());
		addSubcommand(new EmpireEchoCommand());
	}

	@Override
	public String getName()
	{
		return "empire";
	}

	@Override
	public String[] getThisUsage(ICommandSender sender)
	{
		String[] usage = { "... empire <arguments>" };

		return usage;
	}

	@Override
	public String getTitle()
	{
		return "Empire";
	}

}
