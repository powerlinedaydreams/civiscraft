package net.civiscraft.core.net;

import net.civiscraft.core.client.ClientDisplayData;
import net.civiscraft.core.empire.ClientEmpire;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PlayerEmpireAddMessageHandler implements IMessageHandler<PlayerEmpireAddMessage, IMessage>
{
	@Override
	public IMessage onMessage(PlayerEmpireAddMessage message, MessageContext ctx)
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable()
		{
			public void run()
			{
				ClientEmpire empire = message.empire;
				if(empire != null)
				{
					ClientDisplayData.getData().empires.put(empire.id, empire);
					ClientDisplayData.getData().playerEmpire = empire.id;
				}
			}
		});

		return null;
	}
}
