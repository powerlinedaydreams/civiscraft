package net.civiscraft.world.net;

import net.civiscraft.core.client.ClientDisplayData;
import net.civiscraft.world.client.tile.ClientTile;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TileAddMessageHandler implements IMessageHandler<TileAddMessage, IMessage>
{
	@Override
	public IMessage onMessage(TileAddMessage message, MessageContext ctx)
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable()
		{
			public void run()
			{
				ClientTile tile = message.tile;
				ClientDisplayData.getData().tiles.put(tile.pos, tile);
			}
		});

		return null;
	}

}
