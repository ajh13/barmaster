package com.barmaster;

public enum OverheadPlacement
{
	LEFT("Left"),
	RIGHT("Right"),
	ABOVE("Above"),
	BELOW("Below");

	private final String displayName;

	OverheadPlacement(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
