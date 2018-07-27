package net.civiscraft.lib.registry;

import java.util.HashMap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabManager
{
	private static final HashMap<String, CreativeTabCC> tabMap = new HashMap<String, CreativeTabCC>();
	
	public static CreativeTabs getTab(String name)
	{
		if(name.startsWith("vanilla."))
		{
			String after = name.substring("vanilla.".length());
			switch(after)
			{
			case "misc":
				return CreativeTabs.MISC;
			}
		}
		
		if(tabMap.containsKey(name))
		{
			return tabMap.get(name);
		}
		
		else
		{
			throw new IllegalArgumentException("Unknown Tab " + name);
		}
	}
	
	public static CreativeTabCC createTab(String name)
	{
		CreativeTabCC tab = tabMap.get(name);
		
		if(tab != null)
		{
			return tab;
		}
		
		tab = new CreativeTabCC(name);
		tabMap.put(name, tab);
		return tab;
	}
	
	public static void setItem(String name, Item item)
	{
		if(item != null)
		{
			setItem(name, new ItemStack(item));
		}
	}
	
	public static void setItem(String name, ItemStack stack)
	{
		CreativeTabCC tab = tabMap.get(name);
		
		if(tab != null)
		{
			tab.setItem(stack);
		}
	}
	
	public static class CreativeTabCC extends CreativeTabs
	{
		private ItemStack item = new ItemStack(Items.COMPARATOR);
		
		private CreativeTabCC(String name)
		{
			super(name);
		}
		
		public void setItem(Item item)
		{
			if(item != null)
			{
				this.item = new ItemStack(item);
			}
		}
		
		public void setItem(ItemStack stack)
		{
			if(stack == null || stack.isEmpty()) return;
			
			item = stack;
		}
		
		@Override
		public ItemStack getTabIconItem()
		{
			return item;
		}
	}
}
