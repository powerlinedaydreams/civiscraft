package net.civiscraft.core.event;

import net.civiscraft.core.empire.Empire;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EmpireEvent extends Event
{
	private final Empire empire;

	protected EmpireEvent(Empire empire)
	{
		this.empire = empire;
	}

	public Empire getEmpire()
	{
		return empire;
	}

	public static class Create extends EmpireEvent
	{
		public Create(Empire empire)
		{
			super(empire);
		}
	}

	public static class Delete extends EmpireEvent
	{
		private final World world;

		public Delete(Empire empire, World world)
		{
			super(empire);
			this.world = world;
		}

		public World getWorld()
		{
			return world;
		}
	}

	public static class Load extends EmpireEvent
	{
		public Load(Empire empire)
		{
			super(empire);
		}
	}
}
