package net.civiscraft.lib.util;

public class CivisMathUtil
{
	public static float smoothStart2(float f)
	{
		return f * f;
	}

	public static float smoothStart3(float f)
	{
		return f * f * f;
	}

	public static float smoothStart4(float f)
	{
		return f * f * f * f;
	}

	public static float smoothStop2(float f)
	{
		float g = 1 - f;
		return 1 - g * g;
	}

	public static float smoothStop3(float f)
	{
		float g = 1 - f;
		return 1 - g * g * g;
	}

	public static float smoothStop4(float f)
	{
		float g = 1 - f;
		return 1 - g * g * g * g;
	}

	public static float smoothStep2(float f)
	{
		float g = 1 - f;
		return smoothStart2(f) * f + smoothStop2(f) * g;
	}

	public static float smoothStep3(float f)
	{
		float g = 1 - f;
		return smoothStart3(f) * f + smoothStop3(f) * g;
	}

	public static float smoothStep4(float f)
	{
		float g = 1 - f;
		return smoothStart4(f) * f + smoothStop4(f) * g;
	}
}
