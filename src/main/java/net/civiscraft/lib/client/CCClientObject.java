package net.civiscraft.lib.client;

public abstract class CCClientObject
{	
	public boolean isChanged = false;
	
	public void setChanged()
	{
		isChanged = true;
	}
}
