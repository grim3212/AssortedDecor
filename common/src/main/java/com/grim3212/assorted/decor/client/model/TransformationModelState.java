package com.grim3212.assorted.decor.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

public final class TransformationModelState implements ModelState {
    private final Transformation transformation;
    private final boolean uvLocked;

    public TransformationModelState(Transformation transformation, boolean uvLocked) {
        this.transformation = transformation;
        this.uvLocked = uvLocked;
    }

    public TransformationModelState(Transformation transformation) {
        this(transformation, false);
    }

    @Override
    public Transformation getRotation() {
        return transformation;
    }

    @Override
    public boolean isUvLocked() {
        return uvLocked;
    }
}
