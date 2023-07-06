package com.grim3212.assorted.decor.client;

import com.grim3212.assorted.decor.client.blockentity.CageBlockEntityRenderer;
import com.grim3212.assorted.decor.client.blockentity.CalendarBlockEntityRenderer;
import com.grim3212.assorted.decor.client.blockentity.NeonSignBlockEntityRenderer;
import com.grim3212.assorted.decor.client.model.ColorizerUnbakedModel;
import com.grim3212.assorted.decor.client.model.obj.ColorizerObjModel;
import com.grim3212.assorted.decor.client.render.entity.FrameRenderer;
import com.grim3212.assorted.decor.client.render.entity.WallpaperRenderer;
import com.grim3212.assorted.decor.client.screen.CageScreen;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.inventory.DecorContainerTypes;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.decor.config.DecorClientConfig;
import com.grim3212.assorted.lib.mixin.client.AccessorMinecraft;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
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
import java.util.stream.Collectors;

public class DecorClient {

    public static final DecorClientConfig CLIENT_CONFIG = new DecorClientConfig();

    public static void init() {
        ClientServices.CLIENT.registerScreen(DecorContainerTypes.CAGE::get, CageScreen::new);

        ClientServices.CLIENT.registerEntityRenderer(DecorEntityTypes.WALLPAPER::get, WallpaperRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(DecorEntityTypes.WOOD_FRAME::get, FrameRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(DecorEntityTypes.IRON_FRAME::get, FrameRenderer::new);

        ClientServices.CLIENT.registerBlockEntityRenderer(DecorBlockEntityTypes.NEON_SIGN::get, NeonSignBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(DecorBlockEntityTypes.CALENDAR::get, CalendarBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(DecorBlockEntityTypes.CAGE::get, CageBlockEntityRenderer::new);

        DecorBlocks.colorizerBlocks().forEach(x -> ClientServices.CLIENT.registerRenderType(x::get, RenderType.translucent()));
        ClientServices.CLIENT.registerRenderType(DecorBlocks.CLAY_DECORATION::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.BONE_DECORATION::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.CAGE::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.ILLUMINATION_TUBE::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.ILLUMINATION_PLATE::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.ROADWAY_MANHOLE::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.STEEL_DOOR::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.QUARTZ_DOOR::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.GLASS_DOOR::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.CHAIN_LINK_DOOR::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.CHAIN_LINK_FENCE::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.IRON_LANTERN::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.BONE_LANTERN::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.PAPER_LANTERN::get, RenderType.cutout());
        ClientServices.CLIENT.registerRenderType(DecorBlocks.WALL_CLOCK::get, RenderType.cutout());

        ClientServices.CLIENT.registerModelLoader(ColorizerUnbakedModel.LOADER_NAME, ColorizerUnbakedModel.Loader.INSTANCE);
        ClientServices.CLIENT.registerModelLoader(ColorizerObjModel.LOADER_NAME, ColorizerObjModel.Loader.INSTANCE);

        registerBlockColors();
        registerItemColors();
    }

    private static void registerBlockColors() {
        ClientServices.CLIENT.registerBlockColor(new BlockColor() {
            @Override
            public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
                if (pos != null) {
                    BlockEntity te = worldIn.getBlockEntity(pos);
                    if (te != null && te instanceof ColorizerBlockEntity) {
                        return Minecraft.getInstance().getBlockColors().getColor(((ColorizerBlockEntity) te).getStoredBlockState(), worldIn, pos, tint);
                    }
                }
                return 16777215;
            }
        }, () -> DecorBlocks.colorizerBlocks().stream().map(IRegistryObject::get).collect(Collectors.toList()));

        ClientServices.CLIENT.registerBlockColor(new BlockColor() {
            @Override
            public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
                return state.getBlock().defaultMapColor().col;
            }
        }, () -> FluroBlock.FLURO_BY_DYE.values().stream().map(x -> x.get()).collect(Collectors.toList()));

        ClientServices.CLIENT.registerBlockColor(new BlockColor() {
            @Override
            public int getColor(BlockState state, BlockAndTintGetter worldIn, BlockPos pos, int tint) {
                return state.getValue(ColorChangingBlock.COLOR).getMapColor().col;
            }
        }, () -> Arrays.asList(DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get()));
    }

    private static void registerItemColors() {
        ClientServices.CLIENT.registerItemColor(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                if (stack != null && stack.hasTag()) {
                    if (stack.getTag().contains("stored_state")) {
                        BlockState stored = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state"));
                        ItemStack colorStack = new ItemStack(stored.getBlock());
                        if (colorStack.getItem() != null) {
                            return ((AccessorMinecraft) Minecraft.getInstance()).assortedlib_getItemColors().getColor(colorStack, tint);
                        }
                    }
                }
                return 16777215;
            }
        }, () -> DecorBlocks.colorizerBlocks().stream().map(x -> x.get().asItem()).collect(Collectors.toList()));

        ClientServices.CLIENT.registerItemColor(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                if (stack != null && stack.hasTag()) {
                    if (stack.getTag().contains("stored_state")) {
                        BlockState stored = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state"));
                        ItemStack colorStack = new ItemStack(stored.getBlock());
                        if (colorStack.getItem() != null && !(colorStack.getItem() instanceof AirItem)) {
                            return ((AccessorMinecraft) Minecraft.getInstance()).assortedlib_getItemColors().getColor(colorStack, tint);
                        }
                    }
                }
                return 16777215;
            }
        }, () -> Arrays.asList(DecorItems.COLORIZER_BRUSH.get()));

        ClientServices.CLIENT.registerItemColor(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                Block b = Block.byItem(stack.getItem());
                if (b != Blocks.AIR) {
                    return b.defaultMapColor().col;
                }
                return 16777215;
            }
        }, () -> FluroBlock.FLURO_BY_DYE.values().stream().map(x -> x.get().asItem()).collect(Collectors.toList()));

        ClientServices.CLIENT.registerItemColor(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tint) {
                if (stack != null && stack.hasTag() && stack.getTag().contains("BlockStateTag")) {
                    CompoundTag blockState = NBTHelper.getTag(stack.getTag(), "BlockStateTag");
                    if (blockState.contains("color")) {
                        DyeColor color = DyeColor.byName(NBTHelper.getString(blockState, "color"), DyeColor.WHITE);
                        return color.getMapColor().col;
                    }
                }
                return 16777215;
            }
        }, () -> Arrays.asList(DecorBlocks.SIDING_HORIZONTAL.get().asItem(), DecorBlocks.SIDING_VERTICAL.get().asItem()));
    }

}
