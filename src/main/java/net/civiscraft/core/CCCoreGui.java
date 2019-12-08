package net.civiscraft.core;

import java.util.ArrayList;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class CCCoreGui implements IModGuiFactory
{
	public static class GuiConfigManager extends GuiConfig
	{
		public GuiConfigManager(GuiScreen parentScreen)
		{
			super(parentScreen, new ArrayList<>(), "civiscraftcore", "config", false, false,
					I18n.format("config.civiscraft"));

			configElements.add(new ConfigElement(new Property("whatever", "Hello", Property.Type.STRING)));
		}
	}

	public CCCoreGui()
	{
		// Intentionally left blank
	}

	@Override
	public void initialize(Minecraft minecraftInstance)
	{
		// Intentionally left blank
	}

	@Override
	public boolean hasConfigGui()
	{
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new GuiConfigManager(parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}

}
