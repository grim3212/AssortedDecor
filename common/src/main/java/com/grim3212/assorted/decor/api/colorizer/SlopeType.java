package com.grim3212.assorted.decor.api.colorizer;

import com.grim3212.assorted.decor.DecorConfig;

public enum SlopeType {
    SLANTED_CORNER("slanted_corner"),
    CORNER("corner", 2),
    SLOPE("slope", 2),
    SLOPED_ANGLE("sloped_angle", 2),
    OBLIQUE_SLOPE("oblique_slope", 3),
    SLOPED_INTERSECTION("sloped_intersection", 2),
    PYRAMID("pyramid", 2),
    FULL_PYRAMID("full_pyramid", 2),
    SLOPED_POST("sloped_post", 1);

    private final int numPieces;
    private final String name;

    private SlopeType(String name) {
        this(name, -1);
    }

    private SlopeType(String name, int numPieces) {
        this.name = name;
        this.numPieces = numPieces;
    }

    public String getName() {
        return name;
    }

    public int getNumPieces() {
        // Special override for SlantedAngles
        if (this == SLANTED_CORNER)
            return DecorConfig.Common.shapeSmoothness.getValue();
        else
            return numPieces;
    }
}