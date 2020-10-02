package com.grim3212.assorted.decor.common.entity;

import java.util.List;

import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.item.DecorItems;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class WallpaperEntity extends HangingEntity implements IEntityAdditionalSpawnData {

	public boolean isBlockUp;
	public boolean isBlockDown;
	public boolean isBlockLeft;
	public boolean isBlockRight;

	public AxisAlignedBB fireboundingBox;

	private static final DataParameter<Boolean> BURNT = EntityDataManager.<Boolean>createKey(WallpaperEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> WALLPAPER_ID = EntityDataManager.<Integer>createKey(WallpaperEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> COLOR_RED = EntityDataManager.<Integer>createKey(WallpaperEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> COLOR_GREEN = EntityDataManager.<Integer>createKey(WallpaperEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> COLOR_BLUE = EntityDataManager.<Integer>createKey(WallpaperEntity.class, DataSerializers.VARINT);

	public WallpaperEntity(EntityType<? extends WallpaperEntity> type, World world) {
		super(type, world);
		this.isBlockUp = false;
		this.isBlockDown = false;
		this.isBlockLeft = false;
		this.isBlockRight = false;
		this.fireboundingBox = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.facingDirection = Direction.SOUTH;
	}

	public WallpaperEntity(World world) {
		this(DecorEntities.WALLPAPER.get(), world);
	}

	public WallpaperEntity(World world, BlockPos pos, Direction direction) {
		this(world);
		this.hangingPosition = pos;
		this.updateFacingWithBoundingBox(direction);

		List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.fireboundingBox);

		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i) instanceof WallpaperEntity) {
				WallpaperEntity entWallpaper = (WallpaperEntity) entities.get(i);
				this.getDataManager().set(WALLPAPER_ID, entWallpaper.getWallpaperID());

				if (DecorConfig.COMMON.copyDye.get() && !entWallpaper.getBurned()) {
					this.getDataManager().set(COLOR_RED, entWallpaper.getWallpaperColor()[0]);
					this.getDataManager().set(COLOR_GREEN, entWallpaper.getWallpaperColor()[1]);
					this.getDataManager().set(COLOR_BLUE, entWallpaper.getWallpaperColor()[2]);
				}
			}
		}
	}

	@Override
	protected void updateBoundingBox() {
		if (this.facingDirection != null) {
			double d0 = (double) this.hangingPosition.getX() + 0.5D;
			double d1 = (double) this.hangingPosition.getY() + 0.5D;
			double d2 = (double) this.hangingPosition.getZ() + 0.5D;
			double d3 = 0.46875D;
			double d4 = this.offs(this.getWidthPixels());
			double d5 = this.offs(this.getHeightPixels());
			d0 = d0 - (double) this.facingDirection.getXOffset() * d3;
			d2 = d2 - (double) this.facingDirection.getZOffset() * d3;
			d1 = d1 + d5;
			Direction direction = this.facingDirection.rotateYCCW();
			d0 = d0 + d4 * (double) direction.getXOffset();
			d2 = d2 + d4 * (double) direction.getZOffset();
			this.setRawPosition(d0, d1, d2);
			double d6 = (double) this.getWidthPixels();
			double d7 = (double) this.getHeightPixels();
			double d8 = (double) this.getWidthPixels();
			if (this.facingDirection.getAxis() == Direction.Axis.Z) {
				d8 = 1.0D;
			} else {
				d6 = 1.0D;
			}

			d6 = d6 / 32.0D;
			d7 = d7 / 32.0D;
			d8 = d8 / 32.0D;
			this.setBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
			d6 = 1.0F;
			d7 = 1.0F;
			d8 = 1.0F;
			this.fireboundingBox = new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8);
		}
	}

	private double offs(int size) {
		return size % 32 == 0 ? 0.5D : 0.0D;
	}

	@Override
	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty()) {
			if (DecorConfig.COMMON.dyeWallpaper.get()) {
				DyeColor color = DyeColor.getColor(stack);
				if (color != null) {
					dyeWallpaper(color);
					stack.shrink(1);
					return ActionResultType.SUCCESS;
				}

			}

			if (stack.getItem() == DecorItems.WALLPAPER.get()) {
				return updateWallpaper();
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	protected void registerData() {
		this.getDataManager().register(WALLPAPER_ID, 0);
		this.getDataManager().register(COLOR_RED, 255);
		this.getDataManager().register(COLOR_GREEN, 255);
		this.getDataManager().register(COLOR_BLUE, 255);
		this.getDataManager().register(BURNT, false);
	}

	public ActionResultType updateWallpaper() {
		int newWallpaper = this.getWallpaperID() + 1;

		if (newWallpaper >= DecorConfig.COMMON.numWallpapers.get()) {
			newWallpaper = 0;
		}

		this.getDataManager().set(WALLPAPER_ID, newWallpaper);
		if (!this.world.isRemote)
			playPlaceSound();

		return ActionResultType.SUCCESS;
	}

	public boolean updateWallpaper(int wallpaper) {
		this.getDataManager().set(WALLPAPER_ID, wallpaper);

		if (!this.world.isRemote)
			playPlaceSound();

		return true;
	}

	public void dyeWallpaper(DyeColor color, boolean burn) {
		int colorValue = color.getFireworkColor();
		int newred = ((colorValue & 0xFF0000) >> 16);
		int newgreen = ((colorValue & 0xFF00) >> 8);
		int newblue = (colorValue & 0xFF);

		if (color != DyeColor.BLACK) {
			this.getDataManager().set(BURNT, false);
		}

		this.getDataManager().set(COLOR_RED, newred);
		this.getDataManager().set(COLOR_GREEN, newgreen);
		this.getDataManager().set(COLOR_BLUE, newblue);

		if (!this.world.isRemote) {
			if (burn) {
				playBurnSound();
			} else {
				playPlaceSound();
			}
		}
	}

	public void dyeWallpaper(DyeColor color) {
		dyeWallpaper(color, false);
	}

	@Override
	public void tick() {
		if (DecorConfig.COMMON.burnWallpaper.get() && world.func_234853_a_(this.fireboundingBox.expand(-0.001D, -0.001D, -0.001D)).anyMatch((state) -> state.getMaterial() == Material.FIRE) && !this.getBurned()) {
			dyeWallpaper(DyeColor.BLACK, true);
			this.getDataManager().set(BURNT, true);
		}

		super.tick();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean canBeCollidedWith() {
		PlayerEntity player = Minecraft.getInstance().player;

		for (Hand hand : Hand.values()) {
			ItemStack handStack = player.getHeldItem(hand);
			return !handStack.isEmpty() && (handStack.getItem() == DecorItems.WALLPAPER.get() || handStack.getItem() instanceof AxeItem || DyeColor.getColor(handStack) != null);
		}

		return false;
	}

	public int getWallpaperID() {
		return this.getDataManager().get(WALLPAPER_ID);
	}

	public int[] getWallpaperColor() {
		return new int[] { this.getDataManager().get(COLOR_RED), this.getDataManager().get(COLOR_GREEN), this.getDataManager().get(COLOR_BLUE) };
	}

	public boolean getBurned() {
		return this.getDataManager().get(BURNT);
	}

	public void playBurnSound() {
		this.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0F, 1.0F);
	}

	@Override
	public void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		nbt.putByte("Facing", (byte) this.facingDirection.getHorizontalIndex());
		nbt.putInt("Motive", this.getWallpaperID());
		nbt.putInt("Red", this.getWallpaperColor()[0]);
		nbt.putInt("Green", this.getWallpaperColor()[1]);
		nbt.putInt("Blue", this.getWallpaperColor()[2]);
		nbt.putBoolean("Burnt", this.getBurned());
	}

	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		this.facingDirection = Direction.byHorizontalIndex(nbt.getByte("Facing"));
		this.getDataManager().set(WALLPAPER_ID, nbt.getInt("Motive"));
		this.getDataManager().set(COLOR_RED, nbt.getInt("Red"));
		this.getDataManager().set(COLOR_GREEN, nbt.getInt("Green"));
		this.getDataManager().set(COLOR_BLUE, nbt.getInt("Blue"));
		this.getDataManager().set(BURNT, nbt.getBoolean("Burnt"));
		this.updateFacingWithBoundingBox(this.facingDirection);
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		buf.writeBlockPos(this.hangingPosition);
		buf.writeInt(this.getWallpaperID());
		buf.writeInt(this.facingDirection.getHorizontalIndex());
		buf.writeInt(this.getWallpaperColor()[0]);
		buf.writeInt(this.getWallpaperColor()[1]);
		buf.writeInt(this.getWallpaperColor()[2]);
		buf.writeBoolean(this.getBurned());
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.hangingPosition = buf.readBlockPos();
		this.getDataManager().set(WALLPAPER_ID, buf.readInt());
		updateFacingWithBoundingBox(Direction.byHorizontalIndex(buf.readInt()));
		this.getDataManager().set(COLOR_RED, buf.readInt());
		this.getDataManager().set(COLOR_GREEN, buf.readInt());
		this.getDataManager().set(COLOR_BLUE, buf.readInt());
		this.getDataManager().set(BURNT, buf.readBoolean());
	}

	@Override
	public int getWidthPixels() {
		return 16;
	}

	@Override
	public int getHeightPixels() {
		return 16;
	}

	@Override
	public void onBroken(Entity brokenEntity) {
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
			if (brokenEntity instanceof PlayerEntity) {
				PlayerEntity playerentity = (PlayerEntity) brokenEntity;
				if (playerentity.abilities.isCreativeMode) {
					return;
				}
			}

			this.entityDropItem(DecorItems.WALLPAPER.get());
		}
	}

	@Override
	public void playPlaceSound() {
		this.playSound(SoundEvents.BLOCK_WOOL_STEP, 1.0F, 0.8F);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}