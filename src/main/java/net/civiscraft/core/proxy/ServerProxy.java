package net.civiscraft.core.proxy;

import java.awt.image.BufferedImage;

public class ServerProxy extends CCCoreProxy
{
	@Override
	public void preInit()
	{
		BufferedImage image = new BufferedImage(3, 3, 3);
	}
}