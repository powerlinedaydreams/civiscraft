package net.civiscraft.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.civiscraft.core.CCCore;
import net.civiscraft.core.cap.intel.CapPlayerIntel;
import net.civiscraft.core.cap.intel.PlayerIntel;
import net.civiscraft.core.empire.ClientEmpire;
import net.civiscraft.core.empire.Empire;
import net.civiscraft.core.net.PlayerEmpireAddMessage;
import net.civiscraft.core.worldsaveddata.EmpireList;
import net.civiscraft.world.map.tile.TilePos;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class EmpireCommand implements ICommand
{
	private final List aliases;

	public EmpireCommand()
	{
		aliases = new ArrayList<String>();

		aliases.add("empire");
		aliases.add("emp");
	}

	@Override
	public int compareTo(ICommand o)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName()
	{
		return "empire";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "“/empire”: lists all current empires" + "\n - “/empire echo <player id>”: echoes the Player's empire"
				+ "\n - “/empire new <player id>”: creates new empire for the Player"
				+ "\n - “/empire delete <empire id>”: deletes the Empire"
				+ "\n - “/empire addtile <empire id> <tile x> <tile z>";
	}

	@Override
	public List<String> getAliases()
	{
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();

		if(args.length == 0)
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

		else if(args[0].equals("echo"))
		{
			ITextComponent message = new TextComponentString(TextFormatting.GRAY + "You have no current empire");

			if(args.length > 1)
			{
				player = (EntityPlayerMP) server.getWorld(0).getPlayerEntityByName(args[1]);

				if(player == null)
				{
					sender.sendMessage(new TextComponentString(
							TextFormatting.RED + "Empire lookup failed.  Invalid player name."));
					return;
				}

				message = new TextComponentString(TextFormatting.GRAY + player.getName() + " has no current empire.");
			}

			PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);
			UUID empire = intel.getPlayerEmpire();

			if(empire != null && !empire.equals(Empire.nullEmpire))
			{
				message = new TextComponentString(TextFormatting.YELLOW + empire.toString());
			}

			sender.sendMessage(message);

			return;
		}

		else if(args[0].equals("new"))
		{
			if(args.length > 1)
			{
				player = (EntityPlayerMP) server.getWorld(0).getPlayerEntityByName(args[1]);

				if(player == null)
				{
					sender.sendMessage(new TextComponentString(
							TextFormatting.RED + "Empire creation failed.  Invalid player name."));
					return;
				}
			}

			PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);
			if(intel.getPlayerEmpire().equals(Empire.nullEmpire))
			{
				TilePos tilePos = new TilePos(new ChunkPos(player.chunkCoordX, player.chunkCoordZ));
				UUID id = UUID.randomUUID();
				while (id == Empire.nullEmpire)
				{
					id = UUID.randomUUID();
				}
				Empire playerEmpire = new Empire(id, "empireName", player.getUniqueID(), tilePos, player.world);
				intel.setPlayerEmpire(playerEmpire.id);
				CCCore.NETWORK_CHANNEL.sendTo(new PlayerEmpireAddMessage(new ClientEmpire(playerEmpire, player)),
						player);
			}

			else
			{
				ITextComponent message = new TextComponentString(TextFormatting.RED + "Empire creation failed. "
						+ player.getName() + " already belongs to an empire.");
				sender.sendMessage(message);
				return;
			}
		}

		else if(args[0].equals("delete"))
		{
			if(args.length > 1)
			{
				player = (EntityPlayerMP) server.getWorld(0).getPlayerEntityByName(args[1]);

				if(player == null)
				{
					sender.sendMessage(new TextComponentString(
							TextFormatting.RED + "Empire creation failed.  Invalid player name."));
					return;
				}
			}

			PlayerIntel intel = player.getCapability(CapPlayerIntel.CAP, null);
			if(intel.getPlayerEmpire().equals(Empire.nullEmpire))
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

		else if(args[0].equals("addtile"))
		{
			EmpireList empireList = EmpireList.get(server.getWorld(0));
			UUID empireId;

			try
			{
				empireId = UUID.fromString(args[1]);
			}

			catch (IllegalArgumentException e)
			{
				sender.sendMessage(new TextComponentString(
						TextFormatting.RED + "Failed to add tile to empire.  Empire ID was invalid."));
				return;
			}

			Empire empire = empireList.getEmpire(empireId);
			TilePos tilePos;

			if(args.length == 2)
			{
				tilePos = new TilePos(sender.getPosition());
			}

			else if(args.length == 3)
			{
				sender.sendMessage(new TextComponentString(
						TextFormatting.RED + "Failed to add tile to empire. Tile coordinates were invalid."));
				return;
			}

			else
			{
				try
				{
					tilePos = new TilePos(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				}

				catch (NumberFormatException e)
				{
					sender.sendMessage(new TextComponentString(
							TextFormatting.RED + "Failed to add tile to empire. Tile coordinates were invalid."));
					return;
				}
			}

			//update tile and empire with new information
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
