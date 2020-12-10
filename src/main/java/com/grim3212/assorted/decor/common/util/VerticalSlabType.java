package com.grim3212.assorted.decor.common.util;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

// Horizontal facing but with a double option
public enum VerticalSlabType implements IStringSerializable {
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

	public String getString() {
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
