package net.civiscraft.lib.net;

import java.util.EnumMap;
import java.util.HashMap;

import net.civiscraft.lib.CCLibProxy;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.util.MessageUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MessageManager
{
	public static HashMap<Class<?>, MessageManager> managerMap = new HashMap<Class<?>, MessageManager>();
	
	public enum MessageID 
	{
		CCLIB_CACHE_REQUEST(0x00),
		CCLIB_CACHE_RESPONSE(0x01),
		CCLIB_DATA_REQUEST(0x02),
		CCLIB_DATA_RESPONSE(0x03),
		CCWORLD_TILE_UPDATE_ALERT(0x04);
		
		static
		{
			HashMap<Integer, MessageID> ids = new HashMap<Integer, MessageID>();
			for (MessageID type : values())
			{
				MessageID existing = ids.put(type.id, type);
				if(existing != null)
				{
					throw new Error("Duplicate ID's -- " + type + " is the same as " + existing);
				}
			}
		}
		
		public final int id;
		
		MessageID(int id)
		{
			this.id = id;
		}
		
		@Override
		public String toString()
		{
			return name() + " (#" + id + ")";
		}
	}
	
	private static final SimpleNetworkWrapper netWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("cc-lib");
	private static final EnumMap<MessageID, Class<?>> registeredMessages = new EnumMap<>(MessageID.class);
	private static final HashMap<Class<?>, MessageID> knownMessageClasses = new HashMap<Class<?>, MessageID>();
	
	public static <I extends IMessage> void addTypeSent(MessageID id, Class<I> clazz, Side recv)
	{
		addType(id, clazz, null, recv);
	}
	
	public static <I extends IMessage> void addType(MessageID id, Class<I> messageClass, IMessageHandler<I, ?> handler, Side... sides)
	{
		IMessageHandler<I, ?> wrapped;
		Class<?> prev = registeredMessages.get(id);
		
		if (prev != null)
		{
			throw new IllegalStateException("Already registered handler for " + id + " as " + prev + " ( new = " + messageClass + ")");
		}
		
		registeredMessages.put(id, messageClass);
		knownMessageClasses.put(messageClass,  id);
		if(handler != null)
		{
			wrapped = wrapHandler(handler);
		}
		
		else
		{
			wrapped = throwingHandler(messageClass);
		}
		
		if(sides == null || sides.length == 0)
		{
			sides = Side.values();
		}
		
			netWrapper.registerMessage(wrapped,  messageClass,  id.id, Side.CLIENT);
			netWrapper.registerMessage(wrapped,  messageClass,  id.id, Side.SERVER);
	}
	
	private static <I extends IMessage> IMessageHandler<I, ?> throwingHandler(Class<I> clazz)
	{
		return (message, context) ->
		{
			if(context.side == Side.SERVER)
			{
				EntityPlayerMP player = context.getServerHandler().player;
				CCLog.logger.warn("[lib.messages) The client " + player.getName() + " (ID = " + player.getGameProfile().getId() + ") sent an invalid message " + clazz + ", when they should only receive them.");
			}
			
			else
			{
				throw new Error("Received message " + clazz + " on the client, when it should only be sent by the client and received on the server.");
			}
			
			return null;
		};
	}
	
	public static <I extends IMessage> IMessageHandler<I, ?> wrapHandler(IMessageHandler<I, ?> handler)
	{
		return (message, context) ->
		{
			EntityPlayer player = CCLibProxy.getProxy().getPlayerForContext(context);
			
			if(player == null || player.world == null)
			{
				return null;
			}
			
			CCLibProxy.getProxy().addScheduledTask(player.world, () ->
			{
				IMessage reply = handler.onMessage(message, context);
				if(reply != null)
				{
					MessageUtil.sendReturnMessage(context, reply);
				}
			});
			return null;
		};
	}
	
	public static void sendToAll(IMessage message) 
	{
        validateSendingMessage(message);
        netWrapper.sendToAll(message);
    }
	
	public static void sendTo(IMessage message, EntityPlayerMP player) 
	{
        validateSendingMessage(message);
        netWrapper.sendTo(message, player);
    }
	
	public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) 
	{
        validateSendingMessage(message);
        netWrapper.sendToAllAround(message, point);
    }
	
	public static void sendToDimension(IMessage message, int dimensionId) 
	{
        validateSendingMessage(message);
        netWrapper.sendToDimension(message, dimensionId);
    }
	
	public static void sendToServer(IMessage message) 
	{
		validateSendingMessage(message);
        netWrapper.sendToServer(message);
    }
	
	private static void validateSendingMessage(IMessage message) 
	{
        /*if (DEBUG) {
            Class<?> msgClass = message.getClass();
            if (!knownMessageClasses.containsKey(msgClass)) {
                throw new IllegalArgumentException("Unknown/unregistered message " + msgClass);
            }
        }*/
    }
}
