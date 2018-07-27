package net.civiscraft.lib.cap.intel;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import net.civiscraft.lib.CCLib;
import net.civiscraft.lib.cap.CapProvider;
import net.civiscraft.lib.cap.ICap;
import net.civiscraft.lib.util.CapUtil;
import net.civiscraft.lib.util.NBTUtil.NBTType;
import net.civiscraft.world.resource.Resource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapPlayerIntel implements ICap
{
	@CapabilityInject(IIntel.class)
	public static final Capability<IIntel> CAP = null;
	
	public static final EnumFacing DEFAULT_FACING = null;
	
	public static final ResourceLocation ID = new ResourceLocation(CCLib.MODID, "PlayerIntel");

	public static void register() {
		CapabilityManager.INSTANCE.register(IIntel.class, new Capability.IStorage<IIntel>() {
			@Override
			public NBTBase writeNBT(Capability<IIntel> capability, IIntel instance, EnumFacing side) 
			{
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagList empireList = new NBTTagList();
				NBTTagList resourceList = new NBTTagList();
				ArrayList<UUID> empires = instance.getEmpires();
				ArrayList<Resource> resources = instance.getResources();
				
				for(UUID empire : empires)
				{
					NBTTagCompound tag = new NBTTagCompound();
					
					tag.setUniqueId("uuid", empire);
					empireList.appendTag(tag);
				}
				
				for(Resource resource : resources)
				{
					resourceList.appendTag(resource.writeNBT());
				}
				
				nbt.setTag("empires", empireList);
				nbt.setTag("resources", resourceList);
				
				return nbt;
			}

			@Override
			public void readNBT(Capability<IIntel> capability, IIntel instance, EnumFacing side, NBTBase nbtBase) 
			{
				NBTTagCompound nbt = (NBTTagCompound) nbtBase;
				NBTTagList empireList = nbt.getTagList("empires", NBTType.NBTTagCompound.i);
				NBTTagList resourceList = nbt.getTagList("resources", NBTType.NBTTagCompound.i);
				ArrayList<UUID> empires = new ArrayList<UUID>(empireList.tagCount());
				ArrayList<Resource> resources = new ArrayList<Resource>(resourceList.tagCount());
				
				for(NBTBase base : empireList)
				{
					NBTTagCompound tag = (NBTTagCompound) base;
					empires.add(tag.getUniqueId("uuid"));
				}
				
				for(NBTBase base : resourceList)
				{
					NBTTagCompound tag = (NBTTagCompound) base;
					resources.add(Resource.readNBT(tag));
				}
				instance.set(empires, resources);
			}
		}, () -> new PlayerIntel(null));

		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	/**
	 * Get the {@link IIntel} from the specified entity.
	 *
	 * @param entity The entity
	 * @return The ITile
	 */
	@Nullable
	public static IIntel getIntel(EntityPlayer player) {
		return CapUtil.getCapability(player, CAP, DEFAULT_FACING);
	}

	/**
	 * Create a provider for the specified {@link IIntel} instance.
	 *
	 * @param maxHealth The ITile
	 * @return The provider
	 */
	public static ICapabilityProvider createProvider(IIntel playerTiles) {
		return new CapProvider<>(CAP, DEFAULT_FACING, playerTiles);
	}

	/**
	 * Format a max health value.
	 *
	 * @param maxHealth The max health value
	 * @return The formatted text.
	 */

	/**
	 * Event handler for the {@link IIntel} capability.
	 */
	public static class EventHandler {
		/**
		 * Attach the {@link IIntel} capability to all living entities.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public void attachCapabilities(AttachCapabilitiesEvent<Entity> e) {
			if (e.getObject() instanceof EntityPlayer) {
				final PlayerIntel playerIntel = new PlayerIntel((EntityPlayer) e.getObject());
				e.addCapability(ID, createProvider(playerIntel));
			}
		}

		/**
		 * Copy the player's bonus max health when they respawn after dying or returning from the end.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public void playerClone(PlayerEvent.Clone e) {
			final IIntel oldIntel = getIntel(e.getOriginal());
			final IIntel newIntel = getIntel(e.getEntityPlayer());

			if (newIntel != null && oldIntel != null) {
				newIntel.set(oldIntel);
			}
		}
	}
}
