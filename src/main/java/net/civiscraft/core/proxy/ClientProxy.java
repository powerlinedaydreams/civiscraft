package net.civiscraft.core.proxy;

import org.lwjgl.input.Keyboard;

import net.civiscraft.core.CCCoreModels;
import net.civiscraft.core.client.CoreEventHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CCCoreProxy
{
	public static KeyBinding KEY_CREATE_EMPIRE;
	public static KeyBinding KEY_TOP_BAR_LEFT;
	public static KeyBinding KEY_TOP_BAR_RIGHT;
	public static KeyBinding KEY_CLAIM_TILE;

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		/*if(ID == CCCoreGui.LIST.ordinal())
		{
			return new GuiList(player);
		}*/

		return null;
	}

	@Override
	public void preInit()
	{
		CCCoreModels.preInit();
	}

	@Override
	public void init()
	{
		CCCoreModels.init();
		MinecraftForge.EVENT_BUS.register(new CoreEventHandler());
		KEY_CREATE_EMPIRE = getKeyBinding("key.empire.desc", Keyboard.KEY_J, "key.magicbeans.category");
		KEY_TOP_BAR_LEFT = getKeyBinding("key.topbar.left", Keyboard.KEY_LEFT, "key.magicbeans.category");
		KEY_TOP_BAR_RIGHT = getKeyBinding("key.topbar.right", Keyboard.KEY_RIGHT, "key.magicbeans.categry");
		KEY_CLAIM_TILE = getKeyBinding("key.empire.claimtile", Keyboard.KEY_K, "key.magicbeans.category");
	}

	private KeyBinding getKeyBinding(String description, int keyCode, String category)
	{
		KeyBinding result = new KeyBinding(description, keyCode, category);
		ClientRegistry.registerKeyBinding(result);
		return result;
	}
}