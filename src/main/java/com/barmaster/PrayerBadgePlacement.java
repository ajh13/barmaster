package com.barmaster;

public enum PrayerBadgePlacement
{
	LEFT("Left"), RIGHT("Right"), ABOVE("Above"), BELOW("Below");

	private final String displayName;

	PrayerBadgePlacement(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
