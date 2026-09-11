package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

    public void setOwner(Entity newOwner) {
        this.owner = newOwner.getUUID();
    }

    public Entity getOwner() {
        return this.owner != null && this.level instanceof ServerLevel ? ((ServerLevel) this.level).getEntity(this.owner) : null;
    }

    public boolean executeCommand(Player playerIn) {
        for (Component itextcomponent : this.signText) {
            Style style = itextcomponent == null ? null : itextcomponent.getStyle();
            if (style != null && style.getClickEvent() instanceof ClickEvent.RunCommand command) {
                this.level.getServer().getCommands().performPrefixedCommand(this.getCommandSource((ServerPlayer) playerIn), command.command());
            }
        }

        return true;
    }

    public CommandSourceStack getCommandSource(@Nullable ServerPlayer playerIn) {
        String s = playerIn == null ? "Sign" : playerIn.getPlainTextName();
        Component itextcomponent = playerIn == null ? Component.literal("Sign") : playerIn.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel) this.level, LevelBasedPermissionSet.GAMEMASTER, s, itextcomponent, this.level.getServer(), playerIn);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Mode", this.mode);
        output.storeNullable("Owner", UUIDUtil.CODEC, this.owner);

        for (int i = 0; i < 4; ++i) {
            output.store("Text" + (i + 1), ComponentSerialization.CODEC, this.signText[i]);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.mode = input.getIntOr("Mode", 0);
        this.owner = input.read("Owner", UUIDUtil.CODEC).orElse(null);

        for (int i = 0; i < 4; ++i) {
            Component itextcomponent = input.read("Text" + (i + 1), ComponentSerialization.CODEC).orElse(EMPTY);
            this.signText[i] = this.resolveLine(itextcomponent);
        }
    }

    /**
     * Selectors and scores in a line are resolved against the sign itself, exactly like a vanilla sign does
     */
    private MutableComponent resolveLine(Component line) {
        if (this.level instanceof ServerLevel) {
            try {
                return ComponentUtils.resolve(ResolutionContext.create(this.getCommandSource(null)), line).copy();
            } catch (CommandSyntaxException commandsyntaxexception) {
                // fall through to the unresolved line
            }
        }

        return line.copy();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
