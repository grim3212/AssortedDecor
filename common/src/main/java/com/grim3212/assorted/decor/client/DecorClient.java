package com.grim3212.assorted.decor.client;

import com.grim3212.assorted.decor.client.blockentity.CageBlockEntityRenderer;
import com.grim3212.assorted.decor.client.blockentity.CalendarBlockEntityRenderer;
import com.grim3212.assorted.decor.client.blockentity.NeonSignBlockEntityRenderer;
import com.grim3212.assorted.decor.client.render.entity.FrameRenderer;
import com.grim3212.assorted.decor.client.render.entity.WallpaperRenderer;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.client.render.ColorHandlers;
import com.grim3212.assorted.lib.client.render.entity.EntityRenderers;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class DecorClient {
    public static void registerEntityRenderers(EntityRenderers.EntityRendererConsumer consumer) {
        consumer.accept(DecorEntityTypes.WALLPAPER.get(), WallpaperRenderer::new);
        consumer.accept(DecorEntityTypes.WOOD_FRAME.get(), FrameRenderer::new);
        consumer.accept(DecorEntityTypes.IRON_FRAME.get(), FrameRenderer::new);
    }

    public static void registerBlockEntityRenderers(EntityRenderers.BlockEntityRendererConsumer consumer) {
        consumer.accept(DecorBlockEntityTypes.NEON_SIGN.get(), NeonSignBlockEntityRenderer::new);
        consumer.accept(DecorBlockEntityTypes.CALENDAR.get(), CalendarBlockEntityRenderer::new);
        consumer.accept(DecorBlockEntityTypes.CAGE.get(), CageBlockEntityRenderer::new);
    }

    public static void registerBlockColors(BlockColors blockColors, ColorHandlers.BlockHandlerConsumer consumer) {
        consumer.register(new BlockColor() {
            @Override
            public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
                if (pos != null) {
                    BlockEntity te = worldIn.getBlockEntity(pos);
                    if (te != null && te instanceof ColorizerBlockEntity) {
                        return blockColors.getColor(((ColorizerBlockEntity) te).getStoredBlockState(), worldIn, pos, tint);
                    }
                }
                return 16777215;
            }
        }, DecorBlocks.colorizerBlocks());

        consumer.register(new BlockColor() {
            @Override
            public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
                return state.getBlock().defaultMaterialColor().col;
            }
        }, FluroBlock.FLURO_BY_DYE.entrySet().stream().map((x) -> x.getValue().get()).toArray(Block[]::new));

        consumer.register(new BlockColor() {
            @Override
            public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
                return state.getValue(ColorChangingBlock.COLOR).getMaterialColor().col;
            }
        }, DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get());
    }

    public static void registerItemColors(ItemColors itemColors, ColorHandlers.ItemHandlerConsumer consumer) {
        consumer.register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                if (stack != null && stack.hasTag()) {
                    if (stack.getTag().contains("stored_state")) {
                        BlockState stored = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state"));
                        ItemStack colorStack = new ItemStack(stored.getBlock());
                        if (colorStack.getItem() != null) {
                            return itemColors.getColor(colorStack, tint);
                        }
                    }
                }
                return 16777215;
            }
        }, DecorBlocks.colorizerBlocks());

        consumer.register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                if (stack != null && stack.hasTag()) {
                    if (stack.getTag().contains("stored_state")) {
                        BlockState stored = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state"));
                        ItemStack colorStack = new ItemStack(stored.getBlock());
                        if (colorStack.getItem() != null && !(colorStack.getItem() instanceof AirItem)) {
                            return itemColors.getColor(colorStack, tint);
                        }
                    }
                }
                return 16777215;
            }
        }, DecorItems.COLORIZER_BRUSH.get());

        consumer.register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                Block b = Block.byItem(stack.getItem());
                if (b != Blocks.AIR) {
                    return b.defaultMaterialColor().col;
                }
                return 16777215;
            }
        }, FluroBlock.FLURO_BY_DYE.entrySet().stream().map((x) -> x.getValue().get()).toArray(Block[]::new));

        consumer.register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                if (stack != null && stack.hasTag() && stack.getTag().contains("BlockStateTag")) {
                    CompoundTag blockState = NBTHelper.getTag(stack.getTag(), "BlockStateTag");
                    if (blockState.contains("color")) {
                        DyeColor color = DyeColor.byName(NBTHelper.getString(blockState, "color"), DyeColor.WHITE);
                        return color.getMaterialColor().col;
                    }
                }
                return 16777215;
            }
        }, DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get());
    }

    public static void setRenderTypes(BiConsumer<Block, RenderType> consumer) {
        Arrays.stream(DecorBlocks.colorizerBlocks()).forEach(x -> consumer.accept(x, RenderType.translucent()));
        consumer.accept(DecorBlocks.CLAY_DECORATION.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.BONE_DECORATION.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.CAGE.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.ILLUMINATION_TUBE.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.ILLUMINATION_PLATE.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.ROADWAY_MANHOLE.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.STEEL_DOOR.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.QUARTZ_DOOR.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.GLASS_DOOR.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.CHAIN_LINK_DOOR.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.CHAIN_LINK_FENCE.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.IRON_LANTERN.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.BONE_LANTERN.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.PAPER_LANTERN.get(), RenderType.cutout());
        consumer.accept(DecorBlocks.WALL_CLOCK.get(), RenderType.cutout());
    }
}
