package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NeonSignBlockEntity extends BlockEntity {

    public static final MutableComponent EMPTY = Component.literal("");

    public MutableComponent[] signText = new MutableComponent[]{EMPTY, EMPTY, EMPTY, EMPTY};
    private UUID owner;
    public int mode = 0;

    public NeonSignBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public NeonSignBlockEntity(BlockPos pos, BlockState state) {
        super(DecorBlockEntityTypes.NEON_SIGN.get(), pos, state);
    }

    public MutableComponent getText(int line) {
        return this.signText[line];
    }

    public void setText(int line, MutableComponent signText) {
        this.signText[line] = signText;
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setOwner(Entity newOwner) {
        this.owner = newOwner.getUUID();
    }

    public Entity getOwner() {
        return this.owner != null && this.level instanceof ServerLevel ? ((ServerLevel) this.level).getEntity(this.owner) : null;
    }

    public boolean executeCommand(Player playerIn) {
        for (Component itextcomponent : this.signText) {
            Style style = itextcomponent == null ? null : itextcomponent.getStyle();
            if (style != null && style.getClickEvent() != null) {
                ClickEvent clickevent = style.getClickEvent();
                if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    playerIn.getServer().getCommands().performPrefixedCommand(this.getCommandSource((ServerPlayer) playerIn), clickevent.getValue());
                }
            }
        }

        return true;
    }

    public CommandSourceStack getCommandSource(@Nullable ServerPlayer playerIn) {
        String s = playerIn == null ? "Sign" : playerIn.getName().getString();
        Component itextcomponent = (Component) (playerIn == null ? Component.literal("Sign") : playerIn.getDisplayName());
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel) this.level, 2, s, itextcomponent, this.level.getServer(), playerIn);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("Mode", mode);
        compound.putUUID("Owner", owner);

        for (int i = 0; i < 4; ++i) {
            String s = MutableComponent.Serializer.toJson(this.signText[i]);
            compound.putString("Text" + (i + 1), s);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        this.mode = nbt.getInt("Mode");
        this.owner = nbt.getUUID("Owner");

        for (int i = 0; i < 4; ++i) {
            String s = nbt.getString("Text" + (i + 1));
            MutableComponent itextcomponent = MutableComponent.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
            if (this.level instanceof ServerLevel) {
                try {
                    this.signText[i] = ComponentUtils.updateForEntity(this.getCommandSource((ServerPlayer) null), itextcomponent, (Entity) null, 0);
                } catch (CommandSyntaxException commandsyntaxexception) {
                    this.signText[i] = itextcomponent;
                }
            } else {
                this.signText[i] = itextcomponent;
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
