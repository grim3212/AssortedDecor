package com.grim3212.assorted.decor.common.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * The colorizer brush's tooltip line: the block it has picked up, or that it is empty. The block
 * itself stays in the stack's {@code custom_data} under {@code stored_state}; this component only
 * marks the stack as one that shows it.
 */
public record ColorizerBrushInfo() implements TooltipProvider {

    public static final ColorizerBrushInfo INSTANCE = new ColorizerBrushInfo();
    public static final Codec<ColorizerBrushInfo> CODEC = MapCodec.unitCodec(INSTANCE);
    public static final StreamCodec<ByteBuf, ColorizerBrushInfo> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag, DataComponentGetter components) {
        BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompoundOrEmpty("stored_state"));

        if (state.isAir()) {
            tooltip.accept(Component.translatable("tooltip.colorizer_brush.empty"));
        } else {
            tooltip.accept(Component.translatable("tooltip.colorizer_brush.stored", state.getBlock().getName()));
        }
    }
}
