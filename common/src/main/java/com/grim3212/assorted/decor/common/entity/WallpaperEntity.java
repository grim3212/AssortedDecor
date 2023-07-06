package com.grim3212.assorted.decor.common.entity;

import com.grim3212.assorted.decor.DecorCommonMod;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.util.DyeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class WallpaperEntity extends HangingEntity {

    public boolean isBlockUp;
    public boolean isBlockDown;
    public boolean isBlockLeft;
    public boolean isBlockRight;

    public AABB fireboundingBox;

    private static final EntityDataAccessor<Boolean> BURNT = SynchedEntityData.<Boolean>defineId(WallpaperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> WALLPAPER_ID = SynchedEntityData.<Integer>defineId(WallpaperEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR_RED = SynchedEntityData.<Integer>defineId(WallpaperEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR_GREEN = SynchedEntityData.<Integer>defineId(WallpaperEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR_BLUE = SynchedEntityData.<Integer>defineId(WallpaperEntity.class, EntityDataSerializers.INT);

    public WallpaperEntity(EntityType<? extends WallpaperEntity> type, Level world) {
        super(type, world);
        this.isBlockUp = false;
        this.isBlockDown = false;
        this.isBlockLeft = false;
        this.isBlockRight = false;
        this.fireboundingBox = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        this.direction = Direction.SOUTH;
    }

    public WallpaperEntity(EntityType<? extends WallpaperEntity> type, Level level, BlockPos pos, Direction dir) {
        super(type, level, pos);
    }

    public WallpaperEntity(Level world, BlockPos pos, Direction direction) {
        this(DecorEntityTypes.WALLPAPER.get(), world, pos, direction);
        this.pos = pos;
        this.setDirection(direction);

        List<Entity> entities = this.level().getEntities(this, this.fireboundingBox);

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) instanceof WallpaperEntity) {
                WallpaperEntity entWallpaper = (WallpaperEntity) entities.get(i);
                this.getEntityData().set(WALLPAPER_ID, entWallpaper.getWallpaperID());

                if (DecorCommonMod.COMMON_CONFIG.wallpapersCopyDye.get() && !entWallpaper.getBurned()) {
                    this.getEntityData().set(COLOR_RED, entWallpaper.getWallpaperColor()[0]);
                    this.getEntityData().set(COLOR_GREEN, entWallpaper.getWallpaperColor()[1]);
                    this.getEntityData().set(COLOR_BLUE, entWallpaper.getWallpaperColor()[2]);
                }
            }
        }
    }

    @Override
    protected void recalculateBoundingBox() {
        if (this.direction != null) {
            double d0 = (double) this.pos.getX() + 0.5D;
            double d1 = (double) this.pos.getY() + 0.5D;
            double d2 = (double) this.pos.getZ() + 0.5D;
            double d3 = 0.46875D;
            double d4 = this.offs(this.getWidth());
            double d5 = this.offs(this.getHeight());
            d0 = d0 - (double) this.direction.getStepX() * d3;
            d2 = d2 - (double) this.direction.getStepZ() * d3;
            d1 = d1 + d5;
            Direction direction = this.direction.getCounterClockWise();
            d0 = d0 + d4 * (double) direction.getStepX();
            d2 = d2 + d4 * (double) direction.getStepZ();
            this.setPosRaw(d0, d1, d2);
            double d6 = (double) this.getWidth();
            double d7 = (double) this.getHeight();
            double d8 = (double) this.getWidth();
            if (this.direction.getAxis() == Direction.Axis.Z) {
                d8 = 1.0D;
            } else {
                d6 = 1.0D;
            }

            d6 = d6 / 32.0D;
            d7 = d7 / 32.0D;
            d8 = d8 / 32.0D;
            this.setBoundingBox(new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
            d6 = 1.0F;
            d7 = 1.0F;
            d8 = 1.0F;
            this.fireboundingBox = new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8);
        }
    }

    private double offs(int size) {
        return size % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            if (DecorCommonMod.COMMON_CONFIG.dyeWallpapers.get()) {
                DyeColor color = DyeHelper.getColor(stack);
                if (color != null) {
                    dyeWallpaper(color);
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }

            }

            if (stack.getItem() == DecorItems.WALLPAPER.get()) {
                return updateWallpaper();
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(WALLPAPER_ID, 0);
        this.getEntityData().define(COLOR_RED, 255);
        this.getEntityData().define(COLOR_GREEN, 255);
        this.getEntityData().define(COLOR_BLUE, 255);
        this.getEntityData().define(BURNT, false);
    }

    public InteractionResult updateWallpaper() {
        int newWallpaper = this.getWallpaperID() + 1;

        if (newWallpaper >= DecorCommonMod.COMMON_CONFIG.numWallpaperOptions.get()) {
            newWallpaper = 0;
        }

        this.getEntityData().set(WALLPAPER_ID, newWallpaper);
        if (!this.level().isClientSide)
            playPlacementSound();

        return InteractionResult.SUCCESS;
    }

    public boolean updateWallpaper(int wallpaper) {
        this.getEntityData().set(WALLPAPER_ID, wallpaper);

        if (!this.level().isClientSide)
            playPlacementSound();

        return true;
    }

    public void dyeWallpaper(DyeColor color, boolean burn) {
        int colorValue = color.getFireworkColor();
        int newred = ((colorValue & 0xFF0000) >> 16);
        int newgreen = ((colorValue & 0xFF00) >> 8);
        int newblue = (colorValue & 0xFF);

        if (color != DyeColor.BLACK) {
            this.getEntityData().set(BURNT, false);
        }

        this.getEntityData().set(COLOR_RED, newred);
        this.getEntityData().set(COLOR_GREEN, newgreen);
        this.getEntityData().set(COLOR_BLUE, newblue);

        if (!this.level().isClientSide) {
            if (burn) {
                playBurnSound();
            } else {
                playPlacementSound();
            }
        }
    }

    public void dyeWallpaper(DyeColor color) {
        dyeWallpaper(color, false);
    }

    @Override
    public void tick() {
        if (DecorCommonMod.COMMON_CONFIG.wallpapersBurn.get() && level().getBlockStates(this.fireboundingBox.expandTowards(-0.001D, -0.001D, -0.001D)).anyMatch((state) -> state.getBlock() instanceof BaseFireBlock) && !this.getBurned()) {
            dyeWallpaper(DyeColor.BLACK, true);
            this.getEntityData().set(BURNT, true);
        }

        super.tick();
    }

    public int getWallpaperID() {
        return this.getEntityData().get(WALLPAPER_ID);
    }

    public int[] getWallpaperColor() {
        return new int[]{this.getEntityData().get(COLOR_RED), this.getEntityData().get(COLOR_GREEN), this.getEntityData().get(COLOR_BLUE)};
    }

    public boolean getBurned() {
        return this.getEntityData().get(BURNT);
    }

    public void playBurnSound() {
        this.playSound(SoundEvents.FIRE_AMBIENT, 1.0F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("Facing", (byte) this.direction.get2DDataValue());
        nbt.putInt("Motive", this.getWallpaperID());
        nbt.putInt("Red", this.getWallpaperColor()[0]);
        nbt.putInt("Green", this.getWallpaperColor()[1]);
        nbt.putInt("Blue", this.getWallpaperColor()[2]);
        nbt.putBoolean("Burnt", this.getBurned());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.direction = Direction.from2DDataValue(nbt.getByte("Facing"));
        this.getEntityData().set(WALLPAPER_ID, nbt.getInt("Motive"));
        this.getEntityData().set(COLOR_RED, nbt.getInt("Red"));
        this.getEntityData().set(COLOR_GREEN, nbt.getInt("Green"));
        this.getEntityData().set(COLOR_BLUE, nbt.getInt("Blue"));
        this.getEntityData().set(BURNT, nbt.getBoolean("Burnt"));
        this.setDirection(this.direction);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        this.setDirection(Direction.from3DDataValue($$0.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(DecorItems.WALLPAPER.get());
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void dropItem(Entity brokenEntity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.WOOL_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player) {
                Player playerentity = (Player) brokenEntity;
                if (playerentity.getAbilities().instabuild) {
                    return;
                }
            }

            this.spawnAtLocation(DecorItems.WALLPAPER.get());
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.WOOL_STEP, 1.0F, 0.8F);
    }


    @Override
    public boolean shouldRenderAtSqrDistance(double p_31769_) {
        double d0 = 16.0D;
        d0 = d0 * 64.0D * getViewScale();
        return p_31769_ < d0 * d0;
    }

    protected static final Predicate<Entity> HANGING_ENTITY_EXCLUDING_WALLPAPER = (ent) -> {
        return ent instanceof HangingEntity && !(ent instanceof WallpaperEntity);
    };

    private Iterable<VoxelShape> getBlockCollisions(@Nullable Entity ent, AABB aabb) {
        return () -> new BlockCollisions<>(this.level(), ent, aabb, true, (pos, shape) -> shape);
    }

    @Override
    public boolean survives() {
        for (VoxelShape voxelshape : this.getBlockCollisions(this, this.getBoundingBox())) {
            if (!voxelshape.isEmpty()) {
                return false;
            }
        }

        if (!this.level().getEntityCollisions(this, this.getBoundingBox()).isEmpty()) {
            return false;
        } else {
            BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
            BlockState blockstate = this.level().getBlockState(blockpos);

            if (!blockstate.isSolid() && !DiodeBlock.isDiode(blockstate)) {
                return false;
            }

            return this.level().getEntities(this, this.getBoundingBox(), HANGING_ENTITY_EXCLUDING_WALLPAPER).isEmpty();
        }
    }

    @Override
    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        this.setPos(x, y, z);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.setPos(x, y, z);
    }

    @Override
    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }
}