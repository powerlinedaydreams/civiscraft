package net.civiscraft.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration file for lib. In order to keep lib as close to being just a
 * library mod as possible, these are not set by a config file, but instead by
 * CC Core. Feel free to set them yourself, from your own configs, if you do not
 * depend on CC Core itself, and it might not be loaded in the mod environment.
 */
public class CCLibConfig
{

	public static final List<Runnable> configChangeListeners = new ArrayList<Runnable>();

	public static File guiConfigFile = null;

	public static TextureResolution res = TextureResolution.X16;

	public static boolean useHighResTextures = false;

	public static void refreshConfigs()
	{
		for (Runnable r : configChangeListeners)
		{
			r.run();
		}
	}

	public enum TextureResolution
	{
		X16,
		X32,
		X64,
		X128,
		X256,
		X512,
		X1024,
		X2056
	}
}
