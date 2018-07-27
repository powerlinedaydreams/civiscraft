package net.civiscraft.lib.cap;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapProvider<HANDLER> implements ICapabilitySerializable<NBTBase>
{
	/**
	 * The {@link Capability} instance to provide the handler for.
	 */
	private final Capability<HANDLER> capability;

	/**
	 * The {@link EnumFacing} to provide the handler for.
	 */
	private final EnumFacing facing;

	/**
	 * The handler instance to provide.
	 */
	private final HANDLER instance;

	/**
	 * Create a provider for the default handler instance.
	 *
	 * @param capability The Capability instance to provide the handler for
	 * @param facing     The EnumFacing to provide the handler for
	 */
	public CapProvider(Capability<HANDLER> capability, @Nullable EnumFacing facing) 
	{
		this(capability, facing, capability.getDefaultInstance());
	}

	/**
	 * Create a provider for the specified handler instance.
	 *
	 * @param capability The Capability instance to provide the handler for
	 * @param facing     The EnumFacing to provide the handler for
	 * @param instance   The handler instance to provide
	 */
	public CapProvider(Capability<HANDLER> capability, @Nullable EnumFacing facing, HANDLER instance) {
		this.capability = capability;
		this.instance = instance;
		this.facing = facing;
	}

	/**
	 * Determines if this object has support for the capability in question on the specific side.
	 * The return value of this MIGHT change during runtime if this object gains or looses support
	 * for a capability.
	 * <p>
	 * Example:
	 * A Pipe getting a cover placed on one side causing it loose the Inventory attachment function for that side.
	 * <p>
	 * This is a light weight version of getCapability, intended for metadata uses.
	 *
	 * @param capability The capability to check
	 * @param facing     The Side to check from:
	 *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
	 * @return True if this object supports the capability.
	 */
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == getCapability();
	}

	/**
	 * Retrieves the handler for the capability requested on the specific side.
	 * The return value CAN be null if the object does not support the capability.
	 * The return value CAN be the same for multiple faces.
	 *
	 * @param capability The capability to check
	 * @param facing     The Side to check from:
	 *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
	 * @return The handler if this object supports the capability.
	 */
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == getCapability()) {
			return getCapability().cast(getInstance());
		}

		return null;
	}

	@Override
	public NBTBase serializeNBT() {
		return getCapability().writeNBT(getInstance(), getFacing());
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		getCapability().readNBT(getInstance(), getFacing(), nbt);
	}

	/**
	 * Get the {@link Capability} instance to provide the handler for.
	 *
	 * @return The Capability instance
	 */
	public final Capability<HANDLER> getCapability() {
		return capability;
	}

	/**
	 * Get the {@link EnumFacing} to provide the handler for.
	 *
	 * @return The EnumFacing to provide the handler for
	 */
	@Nullable
	public EnumFacing getFacing() {
		return facing;
	}

	/**
	 * Get the handler instance.
	 *
	 * @return The handler instance
	 */
	public final HANDLER getInstance() {
		return instance;
	}
}
