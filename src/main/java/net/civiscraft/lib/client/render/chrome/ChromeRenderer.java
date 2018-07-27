package net.civiscraft.lib.client.render.chrome;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChromeRenderer
{
	public static final ArrayList<IChromeRenderer> renderers = new ArrayList<IChromeRenderer>();
	
	public ChromeRenderer(Minecraft minecraftIn)
	{
		
	}
	
	public static void register(IChromeRenderer renderer)
	{
		renderers.add(renderer);
	}
	
	public void renderChrome(float partialTicks)
	{
		for(IChromeRenderer renderer : renderers)
		{
			renderer.render(partialTicks);
		}
	}
	
	public IChromeRenderer getRenderer(int id)
	{
		return renderers.get(id);
	}
	
	@SideOnly(Side.CLIENT)
	public interface IChromeRenderer
	{
		void render(float partialTicks);
	}
}
