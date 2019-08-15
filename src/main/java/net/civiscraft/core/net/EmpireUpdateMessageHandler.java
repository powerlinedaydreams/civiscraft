package net.civiscraft.core.net;

import net.civiscraft.core.client.ClientDisplayData;
import net.civiscraft.core.empire.ClientEmpire;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EmpireUpdateMessageHandler implements IMessageHandler<EmpireUpdateMessage, IMessage>
{
	@Override
	public IMessage onMessage(EmpireUpdateMessage message, MessageContext ctx)
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable()
		{
			public void run()
			{
				ClientEmpire empire = message.empire;
				ClientDisplayData.getData().empires.put(empire.id, empire);
			}
		});

		return null;
	}
}
