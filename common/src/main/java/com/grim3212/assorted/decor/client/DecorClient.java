package com.grim3212.assorted.decor.client;

import com.grim3212.assorted.decor.client.blockentity.CageBlockEntityRenderer;
import com.grim3212.assorted.decor.client.blockentity.CalendarBlockEntityRenderer;
import com.grim3212.assorted.decor.client.blockentity.NeonSignBlockEntityRenderer;
import com.grim3212.assorted.decor.client.color.BlockMapColorItemTintSource;
import com.grim3212.assorted.decor.client.color.ColorizerItemTintSource;
import com.grim3212.assorted.decor.client.color.SidingItemTintSource;
import com.grim3212.assorted.decor.client.model.ColorizerItemModel;
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
import com.grim3212.assorted.decor.config.DecorClientConfig;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
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

        // TODO(26.2): the block of registerRenderType calls that used to live here is gone.
        //  What it did: told ItemBlockRenderTypes which chunk layer each of the mod's blocks draws in
        //  - translucent for the colorizers, cutout for the doors, fences, lanterns, tubes and so on.
        //  Why it cannot be expressed: ItemBlockRenderTypes was deleted and RenderType lost its
        //  solid()/cutout()/translucent() factories - the chunk layer is a ChunkSectionLayer derived
        //  per quad while baking, from the transparency of the sprite the quad uses (see
        //  BakedQuad.MaterialInfo#of), and lands on BakedQuad.MaterialInfo#layer(). A block declares
        //  its layer from its model json with "render_type" instead, so these blocks' block model
        //  jsons need a "render_type": "minecraft:cutout" (or "minecraft:translucent" for the
        //  colorizers) adding in datagen. AssortedLib keeps IClientHelper#registerRenderType as a
        //  no-op on both loaders; calling it would have looked correct and done nothing.

        ClientServices.CLIENT.registerModelLoader(ColorizerUnbakedModel.LOADER_NAME, ColorizerUnbakedModel.Loader.INSTANCE);
        ClientServices.CLIENT.registerModelLoader(ColorizerObjModel.LOADER_NAME, ColorizerObjModel.Loader.INSTANCE);

        // The item half of the colorizer. A model json loader only produces geometry, so it cannot
        // vary an item with the block the stack has stored; this is the type that can.
        ClientServices.CLIENT.registerItemModelType(ColorizerItemModel.ID, ColorizerItemModel.Unbaked.MAP_CODEC);

        registerBlockColors();
        registerItemTintSources();
    }

    private static void registerBlockColors() {
        // BlockColor became BlockTintSource: colour(state) answers the in-hand colour and
        // colorInWorld(state, level, pos) the placed one, and the tint layer index is the position of
        // the source in the block's list rather than an argument. Colours are ARGB now, so an opaque
        // white is -1 rather than 0xFFFFFF.
        ClientServices.CLIENT.registerBlockColor(new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return -1;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter worldIn, BlockPos pos) {
                BlockEntity te = worldIn.getBlockEntity(pos);
                if (te instanceof ColorizerBlockEntity colorizer) {
                    BlockState stored = colorizer.getStoredBlockState();
                    BlockTintSource source = ClientServices.CLIENT.getBlockColors().getTintSource(stored, 0);
                    if (source != null) {
                        return source.colorInWorld(stored, worldIn, pos);
                    }
                }
                return -1;
            }
        }, () -> DecorBlocks.colorizerBlocks().stream().map(IRegistryObject::get).collect(Collectors.toList()));

        ClientServices.CLIENT.registerBlockColor(state -> ARGB.opaque(state.getBlock().defaultMapColor().col), () -> FluroBlock.FLURO_BY_DYE.values().stream().map(x -> x.get()).collect(Collectors.toList()));

        ClientServices.CLIENT.registerBlockColor(state -> ARGB.opaque(state.getValue(ColorChangingBlock.COLOR).getMapColor().col), () -> Arrays.asList(DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get()));
    }

    // TODO(26.2): registering these codecs is only half of what the four ItemColor handlers used to do.
    //  ItemColor / ItemColors were deleted; an item's tints are a list of ItemTintSource entries in its
    //  item model json and code only registers the MapCodec that reads a custom type. Each of the item
    //  models that used to be covered by a handler therefore needs a "tints" entry naming the matching
    //  id - assorteddecor:colorizer for the colorizer block items and the colorizer brush,
    //  assorteddecor:block_map_color for the fluro block items, assorteddecor:siding for the two siding
    //  items - which is a datagen change this class cannot make.
    private static void registerItemTintSources() {
        ClientServices.CLIENT.registerItemTintSource(ColorizerItemTintSource.ID, ColorizerItemTintSource.MAP_CODEC);
        ClientServices.CLIENT.registerItemTintSource(BlockMapColorItemTintSource.ID, BlockMapColorItemTintSource.MAP_CODEC);
        ClientServices.CLIENT.registerItemTintSource(SidingItemTintSource.ID, SidingItemTintSource.MAP_CODEC);
    }
}
