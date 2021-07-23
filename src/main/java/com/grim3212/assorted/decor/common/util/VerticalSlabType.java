package com.grim3212.assorted.decor.common.util;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

// Horizontal facing but with a double option
public enum VerticalSlabType implements StringRepresentable {
	NORTH("north"),
	SOUTH("south"),
	EAST("east"),
	WEST("west"),
	DOUBLE("double");

	private final String name;

	private VerticalSlabType(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}

	public static VerticalSlabType fromDirection(Direction dir) {
		switch (dir) {
		case EAST:
			return EAST;
		case SOUTH:
			return SOUTH;
		case WEST:
			return WEST;
		case NORTH:
		default:
			return NORTH;

		}
	}
	
	public Direction toDirection() {
		switch (this) {
		case EAST:
			return Direction.EAST;
		case SOUTH:
			return Direction.SOUTH;
		case WEST:
			return Direction.WEST;
		case NORTH:
		default:
			return Direction.NORTH;

		}
	}
}
