package com.grim3212.assorted.decor.common.blocks;

import net.minecraft.world.level.block.IronBarsBlock;

/**
 * Vanilla's IronBarsBlock constructor is protected, so the chain link fence needs a public entry
 * point of its own
 */
public class DecorBarsBlock extends IronBarsBlock {

    public DecorBarsBlock(Properties props) {
        super(props);
    }
}
