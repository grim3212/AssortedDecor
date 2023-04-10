package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.api.util.VerticalSlabType;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DecorBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    public DecorBlockLoot() {
        super(() -> DecorBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));

        blocks.add(DecorBlocks.PLANTER_POT.get());
        blocks.add(DecorBlocks.CLAY_DECORATION.get());
        blocks.add(DecorBlocks.BONE_DECORATION.get());
        blocks.add(DecorBlocks.PAPER_LANTERN.get());
        blocks.add(DecorBlocks.BONE_LANTERN.get());
        blocks.add(DecorBlocks.IRON_LANTERN.get());
        blocks.add(DecorBlocks.SIDEWALK.get());
        blocks.add(DecorBlocks.ROADWAY.get());
        blocks.add(DecorBlocks.ROADWAY_LIGHT.get());
        blocks.add(DecorBlocks.ROADWAY_MANHOLE.get());
        blocks.add(DecorBlocks.CHAIN_LINK_FENCE.get());
        blocks.add(DecorBlocks.FOUNTAIN.get());
        blocks.add(DecorBlocks.DECORATIVE_STONE.get());
        blocks.add(DecorBlocks.STONE_PATH.get());
        blocks.add(DecorBlocks.CAGE.get());

        DecorBlocks.ROADWAY_COLORS.forEach((c, r) -> blocks.add(r.get()));

        blocks.add(DecorBlocks.COLORIZER.get());
        blocks.add(DecorBlocks.COLORIZER_CHAIR.get());
        blocks.add(DecorBlocks.COLORIZER_TABLE.get());
        blocks.add(DecorBlocks.COLORIZER_STOOL.get());
        blocks.add(DecorBlocks.COLORIZER_COUNTER.get());
        blocks.add(DecorBlocks.COLORIZER_FENCE.get());
        blocks.add(DecorBlocks.COLORIZER_FENCE_GATE.get());
        blocks.add(DecorBlocks.COLORIZER_WALL.get());
        blocks.add(DecorBlocks.COLORIZER_STAIRS.get());
        blocks.add(DecorBlocks.COLORIZER_TRAP_DOOR.get());
        blocks.add(DecorBlocks.COLORIZER_LAMP_POST.get());
        blocks.add(DecorBlocks.COLORIZER_SLOPE.get());
        blocks.add(DecorBlocks.COLORIZER_SLOPED_ANGLE.get());
        blocks.add(DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get());
        blocks.add(DecorBlocks.COLORIZER_SLOPED_POST.get());
        blocks.add(DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get());
        blocks.add(DecorBlocks.COLORIZER_CORNER.get());
        blocks.add(DecorBlocks.COLORIZER_SLANTED_CORNER.get());
        blocks.add(DecorBlocks.COLORIZER_PYRAMID.get());
        blocks.add(DecorBlocks.COLORIZER_FULL_PYRAMID.get());

        blocks.add(DecorBlocks.COLORIZER_CHIMNEY.get());
        blocks.add(DecorBlocks.COLORIZER_FIREPLACE.get());
        blocks.add(DecorBlocks.COLORIZER_FIREPIT.get());
        blocks.add(DecorBlocks.COLORIZER_FIREPIT_COVERED.get());
        blocks.add(DecorBlocks.COLORIZER_FIRERING.get());
        blocks.add(DecorBlocks.COLORIZER_STOVE.get());

        blocks.add(DecorBlocks.NEON_SIGN.get());

        blocks.add(DecorBlocks.ILLUMINATION_TUBE.get());
        blocks.add(DecorBlocks.ILLUMINATION_PLATE.get());
        blocks.add(DecorBlocks.CALENDAR.get());
        blocks.add(DecorBlocks.WALL_CLOCK.get());

        FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> blocks.add(x.getValue().get()));
    }

    @Override
    public void generate() {
        for (Block b : blocks) {
            this.dropSelf(b);
        }

        this.add(DecorBlocks.COLORIZER_DOOR.get(), createDoorTable(DecorBlocks.COLORIZER_DOOR.get()));
        this.add(DecorBlocks.QUARTZ_DOOR.get(), createDoorTable(DecorBlocks.QUARTZ_DOOR.get()));
        this.add(DecorBlocks.CHAIN_LINK_DOOR.get(), createDoorTable(DecorBlocks.CHAIN_LINK_DOOR.get()));
        this.add(DecorBlocks.GLASS_DOOR.get(), createDoorTable(DecorBlocks.GLASS_DOOR.get()));
        this.add(DecorBlocks.STEEL_DOOR.get(), createDoorTable(DecorBlocks.STEEL_DOOR.get()));

        this.add(DecorBlocks.COLORIZER_SLAB.get(), createSlabItemTable(DecorBlocks.COLORIZER_SLAB.get()));
        this.add(DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), createVerticalSlabItemTable(DecorBlocks.COLORIZER_VERTICAL_SLAB.get()));

        this.add(DecorBlocks.SIDING_VERTICAL.get(), createColorTable(DecorBlocks.SIDING_VERTICAL.get()));
        this.add(DecorBlocks.SIDING_HORIZONTAL.get(), createColorTable(DecorBlocks.SIDING_HORIZONTAL.get()));

    }

    private LootTable.Builder createVerticalSlabItemTable(Block b) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(applyExplosionDecay(b, LootItem.lootTableItem(b).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE)))))));
    }

    private LootTable.Builder createColorTable(Block b) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
        CopyBlockState.Builder func = CopyBlockState.copyState(b).copy(ColorChangingBlock.COLOR);
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion()).apply(func);
        return LootTable.lootTable().withPool(pool);
    }
}
