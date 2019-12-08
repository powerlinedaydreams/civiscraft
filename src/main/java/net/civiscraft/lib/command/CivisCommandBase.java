package net.civiscraft.lib.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;

public abstract class CivisCommandBase extends CommandBase
{
	public abstract String getDescription();

	public abstract String[] getThisUsage(ICommandSender sender);

	public abstract String getTitle();

	@Override
	public String getUsage(ICommandSender sender)
	{
		String[] usage = getThisUsage(sender);
		String result = "\n" + TextFormatting.BOLD + getTitle() + TextFormatting.RESET + " - " + getDescription();
		for (int j = 0; j < usage.length; j++)
		{
			result += "\n  " + TextFormatting.ITALIC + usage[j] + TextFormatting.RESET;
		}
		result += "\n——————";

		return result;
	}
}
