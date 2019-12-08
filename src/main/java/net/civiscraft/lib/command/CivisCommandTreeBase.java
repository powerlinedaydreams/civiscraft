package net.civiscraft.lib.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.command.CommandTreeBase;

public abstract class CivisCommandTreeBase extends CommandTreeBase
{
	@Override
	public String getUsage(ICommandSender sender)
	{
		String result = "----" + TextFormatting.BOLD + getTitle() + TextFormatting.RESET + "----";

		String[] thisUsage = getThisUsage(sender);

		for (int i = 0; i < thisUsage.length; i++)
		{
			result += "/n  " + TextFormatting.ITALIC + thisUsage[i] + TextFormatting.RESET;
		}

		String[] subUsage = getSubUsageArray(sender);

		for (int i = 0; i < subUsage.length; i++)
		{
			result += "\n|" + subUsage[i];
		}

		result += "\n";

		return result;
	}

	public String[] getSubUsageArray(ICommandSender sender)
	{
		List<String> result = new ArrayList<String>();
		Object[] commands = getSubCommands().toArray();

		for (int i = 0; i < commands.length; i++)
		{
			if(commands[i] instanceof CivisCommandTreeBase)
			{
				CivisCommandTreeBase command = (CivisCommandTreeBase) commands[i];

				result.add(command.getUsage(sender));
				continue;
			}

			CivisCommandBase command = (CivisCommandBase) commands[i];
			String[] usage = command.getThisUsage(sender);
			result.add("\n" + TextFormatting.BOLD + command.getTitle() + TextFormatting.RESET + " - "
					+ command.getDescription());
			for (int j = 0; j < usage.length; j++)
			{
				result.add("\n  " + TextFormatting.ITALIC + usage[j] + TextFormatting.RESET);
			}
			result.add("\n——————");
		}

		String[] resultPackage = new String[result.size()];

		return result.toArray(resultPackage);
	}

	public abstract String[] getThisUsage(ICommandSender sender);

	public abstract String getTitle();
}
