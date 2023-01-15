package com.grim3212.assorted.decor.common.entity;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.item.FrameItem.FrameMaterial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class FrameEntity extends HangingEntity implements IEntityAdditionalSpawnData {

	private FrameMaterial material;
	protected float resistance = 0.0F;
	private AABB fireboundingBox = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	private static final EntityDataAccessor<Boolean> BURNT = SynchedEntityData.defineId(FrameEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> FRAME_ID = SynchedEntityData.defineId(FrameEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> COLOR_RED = SynchedEntityData.defineId(FrameEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> COLOR_GREEN = SynchedEntityData.defineId(FrameEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> COLOR_BLUE = SynchedEntityData.defineId(FrameEntity.class, EntityDataSerializers.INT);

	public FrameEntity(EntityType<? extends FrameEntity> type, Level world) {
		super(type, world);
	}

	public FrameEntity(Level world) {
		this(DecorEntityTypes.FRAME.get(), world);
	}

	public FrameEntity(Level world, BlockPos pos, Direction direction, FrameMaterial type) {
		this(world);
		this.pos = pos;
		this.material = type;

		for (int i = 0; i < FrameType.VALUES.length; i++) {
			FrameType tryFrame = FrameType.VALUES[i];
			this.getEntityData().set(FRAME_ID, tryFrame.id);
			setDirection(direction);
			if (survives()) {
				// For the first valid direction update the frame and you don't
				// need to search anymore
				break;
			}
		}

		setResistance(this.material);
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(FRAME_ID, 1);
		this.getEntityData().define(COLOR_RED, 255);
		this.getEntityData().define(COLOR_GREEN, 255);
		this.getEntityData().define(COLOR_BLUE, 255);
		this.getEntityData().define(BURNT, false);
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (player.mayUseItemAt(pos, this.direction, itemstack)) {
			if (!itemstack.isEmpty()) {

				if (DecorConfig.COMMON.dyeFrames.get()) {
					DyeColor color = DyeColor.getColor(itemstack);
					if (color != null) {
						if (dyeFrame(color)) {
							if (!player.getAbilities().instabuild) {
								itemstack.shrink(1);
							}
							return InteractionResult.SUCCESS;
						}
					}
				}

				if (itemstack.getItem() == this.material.getFrameItem()) {
					return updateFrame() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
				}
			}
		}
		return InteractionResult.FAIL;
	}

	public boolean updateFrame() {
		boolean foundOld = false;
		boolean looking = true;
		int oldFrameID = this.getFrameID();

		// Wrapped in a while to account for if the the next valid frame is at
		// the beginning of the FrameType values
		while (looking) {
			for (int i = 0; i < FrameType.VALUES.length; i++) {
				FrameType tryFrame = FrameType.VALUES[i];
				this.getEntityData().set(FRAME_ID, tryFrame.id);
				setDirection(this.direction);
				if (survives()) {
					if (foundOld) {
						// The next valid frame we stop looking for the next
						// frame to use
						looking = false;
						break;
					} else {
						if (tryFrame.id == oldFrameID) {
							foundOld = true;
						}
					}
				} else {
					if (tryFrame.id == oldFrameID) {
						looking = false;
						break;
					}
				}
			}
		}

		if (!this.level.isClientSide)
			playPlacementSound();

		return true;
	}

	public boolean dyeFrame(DyeColor color, boolean burn) {
		int colorValue = color.getFireworkColor();
		int newred = (colorValue & 0xFF0000) >> 16;
		int newgreen = (colorValue & 0xFF00) >> 8;
		int newblue = colorValue & 0xFF;

		if (newred == this.getFrameColor()[0] && newgreen == this.getFrameColor()[1] && newblue == this.getFrameColor()[2]) {
			return false;
		}

		this.getEntityData().set(COLOR_RED, newred);
		this.getEntityData().set(COLOR_GREEN, newgreen);
		this.getEntityData().set(COLOR_BLUE, newblue);
		this.getEntityData().set(BURNT, burn);

		if (!this.level.isClientSide) {
			if (burn) {
				playBurnSound();
			} else {
				playPlacementSound();
			}
		}

		return true;
	}

	public boolean dyeFrame(DyeColor color) {
		return dyeFrame(color, false);
	}

	public void setResistance(FrameMaterial material) {
		switch (material) {
			case WOOD:
				this.resistance = 9.0F;
				break;
			case IRON:
				this.resistance = 18.0F;
		}
	}

	@Override
	protected void recalculateBoundingBox() {
		if (this.direction != null) {
			double x = (double) this.pos.getX() + 0.5D;
			double y = (double) this.pos.getY() + 0.5D;
			double z = (double) this.pos.getZ() + 0.5D;
			double widthOffset = this.offs(this.getWidth());
			double heightOffset = this.offs(this.getHeight());
			x = x - (double) this.direction.getStepX() * 0.46875D;
			z = z - (double) this.direction.getStepZ() * 0.46875D;
			y = y + heightOffset;
			Direction enumfacing = this.direction.getCounterClockWise();
			x = x + widthOffset * (double) enumfacing.getStepX();
			z = z + widthOffset * (double) enumfacing.getStepZ();
			this.setPosRaw(x, y, z);
			double width = (double) this.getWidth();
			double height = (double) this.getHeight();
			double depth = (double) this.getWidth();

			if (this.direction.getAxis() == Direction.Axis.Z) {
				depth = 1.0D;
			} else {
				width = 1.0D;
			}

			width = width / 32.0D;
			height = height / 32.0D;
			depth = depth / 32.0D;

			this.setBoundingBox(new AABB(x - width, y - height, z - depth, x + width, y + height, z + depth));

			if (this.direction.getAxis() == Direction.Axis.Z) {
				width += 0.1F;
				height += 0.1F;
				depth = 1.0F;
			} else {
				width = 1.0F;
				height += 0.1F;
				depth += 0.1F;
			}
			this.fireboundingBox = new AABB(x - width, y - height, z - depth, x + width, y + height, z + depth);
		}

	}

	private double offs(int size) {
		return size % 32 == 0 ? 0.5D : 0.0D;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		BlockPos blockpos = this.pos.offset(x - this.getX(), y - this.getY(), z - this.getZ());
		this.setPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
		this.setRot(yaw, pitch);
	}

	@Override
	public void tick() {
		if (this.material == FrameMaterial.WOOD && DecorConfig.COMMON.burnFrames.get()) {
			if (this.level.getBlockStates(this.fireboundingBox.expandTowards(-0.001D, -0.001D, -0.001D)).anyMatch((state) -> state.getMaterial() == Material.FIRE) && !this.getBurned()) {
				dyeFrame(DyeColor.BLACK, true);
			}
		}
	}

	public FrameType getCurrentFrame() {
		return FrameType.getFrameById(this.getEntityData().get(FRAME_ID));
	}

	@Override
	public boolean canCollideWith(Entity ent) {
		return ent instanceof Player && this.canBeCollidedWith();
	}

	@Override
	public boolean canBeCollidedWith() {
		return getCurrentFrame().isCollidable;
	}

	@Override
	public boolean isPickable() {
		Player player = AssortedDecor.proxy.getClientPlayer();
		if (player == null)
			return false;

		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack handStack = player.getItemInHand(hand);

			if (!handStack.isEmpty()) {
				if (handStack.getItem() == this.material.getFrameItem() || this.material.effectiveTool().isInstance(handStack.getItem()) || DyeColor.getColor(handStack) != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hurt(DamageSource damagesource, float damage) {
		if (this.isInvulnerableTo(damagesource)) {
			return false;
		} else {
			if (!this.isRemoved() && !this.level.isClientSide) {
				if (damagesource.getEntity() instanceof Player) {
					this.discard();
					this.markHurt();
					this.dropItem(damagesource.getEntity());
					return true;
				}

				if (damage > this.resistance) {
					this.discard();
					this.markHurt();
					this.dropItem(damagesource.getEntity());
					return true;
				}
			}

			return false;
		}
	}

	public int getFrameID() {
		return this.getEntityData().get(FRAME_ID);
	}

	public int[] getFrameColor() {
		return new int[] { this.getEntityData().get(COLOR_RED), this.getEntityData().get(COLOR_GREEN), this.getEntityData().get(COLOR_BLUE) };
	}

	public boolean getBurned() {
		return this.getEntityData().get(BURNT);
	}

	public void playBurnSound() {
		this.playSound(SoundEvents.FIRE_AMBIENT, 1.0F, 1.0F);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbttagcompound) {
		super.addAdditionalSaveData(nbttagcompound);
		nbttagcompound.putByte("Facing", (byte) this.direction.get2DDataValue());
		nbttagcompound.putInt("Motive", this.getFrameID());
		int[] color = getFrameColor();
		nbttagcompound.putInt("Red", color[0]);
		nbttagcompound.putInt("Green", color[1]);
		nbttagcompound.putInt("Blue", color[2]);
		nbttagcompound.putInt("Material", this.material.ordinal());
		nbttagcompound.putBoolean("Burnt", getBurned());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbttagcompound) {
		super.readAdditionalSaveData(nbttagcompound);
		this.direction = Direction.from2DDataValue(nbttagcompound.getByte("Facing"));
		this.getEntityData().set(COLOR_RED, nbttagcompound.getInt("Red"));
		this.getEntityData().set(COLOR_GREEN, nbttagcompound.getInt("Green"));
		this.getEntityData().set(COLOR_BLUE, nbttagcompound.getInt("Blue"));
		setResistance(this.material = FrameMaterial.values()[nbttagcompound.getInt("Material")]);
		this.getEntityData().set(FRAME_ID, nbttagcompound.getInt("Motive"));
		this.getEntityData().set(BURNT, nbttagcompound.getBoolean("Burnt"));
		this.setDirection(this.direction);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf data) {
		data.writeInt(this.getFrameID());
		data.writeBlockPos(this.pos);
		data.writeEnum(this.material);
		data.writeInt(this.direction.get2DDataValue());

		int[] color = getFrameColor();
		data.writeInt(color[0]);
		data.writeInt(color[1]);
		data.writeInt(color[2]);
		data.writeBoolean(this.getBurned());
	}

	@Override
	public void readSpawnData(FriendlyByteBuf data) {
		this.getEntityData().set(FRAME_ID, data.readInt());

		this.pos = data.readBlockPos();
		this.material = data.readEnum(FrameMaterial.class);
		setResistance(this.material);
		setDirection(Direction.from2DDataValue(data.readInt()));

		this.getEntityData().set(COLOR_RED, data.readInt());
		this.getEntityData().set(COLOR_GREEN, data.readInt());
		this.getEntityData().set(COLOR_BLUE, data.readInt());
		this.getEntityData().set(BURNT, data.readBoolean());
	}

	@Override
	public int getWidth() {
		return this.getCurrentFrame().sizeX;
	}

	@Override
	public int getHeight() {
		return this.getCurrentFrame().sizeY;
	}

	@Override
	public void dropItem(Entity brokenEntity) {
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.playBreakSound();
			if (brokenEntity instanceof Player) {
				Player playerentity = (Player) brokenEntity;
				if (playerentity.getAbilities().instabuild) {
					return;
				}
			}

			this.spawnAtLocation(this.material.getFrameItem());
		}
	}

	public void playBreakSound() {
		switch (this.material) {
			case WOOD:
				this.playSound(SoundEvents.WOOD_BREAK, 1.0F, 1.0F);
				break;
			case IRON:
				this.playSound(SoundEvents.METAL_BREAK, 1.0F, 1.0F);
		}
	}

	@Override
	public void playPlacementSound() {
		switch (this.material) {
			case WOOD:
				this.playSound(SoundEvents.WOOD_PLACE, 1.0F, 1.0F);
				break;
			case IRON:
				this.playSound(SoundEvents.METAL_PLACE, 1.0F, 1.0F);
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public FrameMaterial getMaterial() {
		return material;
	}

	public static enum FrameType {
		frame_01(1, 32, 32, new int[] { 0, 1, 2, 3 }, true),
		frame_19(19, 32, 32, new int[] { 35, 36, 37 }, false),
		frame_07(7, 64, 32, new int[] { 10, 11, 12, 13 }, true),
		frame_10(10, 64, 32, new int[] { 14, 15, 10, 11, 12, 13 }, true),
		frame_11(11, 64, 32, new int[] { 16, 17, 10, 11, 12, 13 }, true),
		frame_37(37, 48, 32, new int[] { 10, 11, 12, 13 }, true),
		frame_12(12, 48, 32, new int[] { 14, 15, 10, 11, 12, 13 }, true),
		frame_13(13, 48, 32, new int[] { 16, 17, 10, 11, 12, 13 }, true),
		frame_14(14, 32, 32, new int[] { 0, 1, 2, 3, 4, 5 }, true),
		frame_02(2, 32, 32, new int[] { 0, 1, 2, 3, 4 }, true),
		frame_03(3, 32, 32, new int[] { 0, 1, 2, 3, 5 }, true),
		frame_04(4, 32, 32, new int[] { 6, 7, 0, 1, 2, 3 }, true),
		frame_05(5, 32, 32, new int[] { 8, 9, 0, 1, 2, 3 }, true),
		frame_06(6, 32, 32, new int[] { 6, 7, 8, 9, 0, 1, 2, 3 }, true),
		frame_08(8, 32, 32, new int[] { 18, 0, 3 }, true),
		frame_09(9, 32, 32, new int[] { 19, 0, 1 }, true),
		frame_23(23, 32, 32, new int[] { 40, 1, 2 }, true),
		frame_24(24, 32, 32, new int[] { 41, 2, 3 }, true),
		frame_31(31, 32, 32, new int[] { 54, 55 }, true),
		frame_32(32, 32, 32, new int[] { 56, 57 }, true),
		frame_39(39, 32, 32, new int[] { 54, 55, 56, 57 }, true),
		frame_15(15, 16, 16, new int[] { 20, 21, 22, 23 }, true),
		frame_16(16, 32, 16, new int[] { 24, 25, 26, 27 }, true),
		frame_17(17, 16, 32, new int[] { 28, 29, 30, 31 }, true),
		frame_18(18, 16, 32, new int[] { 32, 33, 34 }, false),
		frame_40(40, 16, 32, new int[] { 46, 47 }, true),
		frame_41(41, 16, 32, new int[] { 48, 49 }, true),
		frame_42(42, 16, 32, new int[] { 46, 47, 48, 49 }, true),
		frame_29(29, 16, 16, new int[] { 46, 47 }, true),
		frame_30(30, 16, 16, new int[] { 48, 49 }, true),
		frame_38(38, 16, 16, new int[] { 46, 47, 48, 49 }, true),
		frame_25(25, 16, 16, new int[] { 42, 20, 23 }, true),
		frame_28(28, 16, 16, new int[] { 45, 22, 23 }, true),
		frame_26(26, 16, 16, new int[] { 44, 20, 21 }, true),
		frame_27(27, 16, 16, new int[] { 43, 21, 22 }, true),
		frame_20(20, 16, 16, new int[] { 38 }, true),
		frame_21(21, 16, 16, new int[] { 39 }, true),
		frame_22(22, 16, 16, new int[] { 38, 39 }, true),
		frame_33(33, 16, 16, new int[] { 50 }, false),
		frame_35(35, 16, 16, new int[] { 52 }, false),
		frame_34(34, 16, 16, new int[] { 51 }, false),
		frame_36(36, 16, 16, new int[] { 53 }, false);

		public static final int maxArtTitleLength = "frame_01".length();
		public static final FrameType[] VALUES = FrameType.values();
		public final int id;
		public final int sizeX;
		public final int sizeY;
		public final int[] planks;
		public final boolean isCollidable;

		private FrameType(int id, int x, int y, int[] planks, boolean collidable) {
			this.id = id;
			this.sizeX = x;
			this.sizeY = y;
			this.planks = planks;
			this.isCollidable = collidable;
		}

		public static FrameType getFrameById(int id) {
			for (int i = 0; i < FrameType.values().length; i++) {
				if (FrameType.values()[i].id == id)
					return FrameType.values()[i];
			}

			return null;
		}
	}
}