package com.grim3212.assorted.decor.common.entity;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.item.FrameItem.FrameMaterial;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class FrameEntity extends HangingEntity implements IEntityAdditionalSpawnData {

	private FrameMaterial material;
	protected float resistance = 0.0F;
	private AxisAlignedBB fireboundingBox = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	private static final DataParameter<Boolean> BURNT = EntityDataManager.createKey(FrameEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> FRAME_ID = EntityDataManager.createKey(FrameEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> COLOR_RED = EntityDataManager.createKey(FrameEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> COLOR_GREEN = EntityDataManager.createKey(FrameEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> COLOR_BLUE = EntityDataManager.createKey(FrameEntity.class, DataSerializers.VARINT);

	public FrameEntity(EntityType<? extends FrameEntity> type, World world) {
		super(type, world);
	}

	public FrameEntity(World world) {
		this(DecorEntityTypes.FRAME.get(), world);
	}

	public FrameEntity(World world, BlockPos pos, Direction direction, FrameMaterial type) {
		this(world);
		this.hangingPosition = pos;
		this.material = type;

		for (int i = 0; i < FrameType.VALUES.length; i++) {
			FrameType tryFrame = FrameType.VALUES[i];
			this.getDataManager().set(FRAME_ID, tryFrame.id);
			updateFacingWithBoundingBox(direction);
			if (onValidSurface()) {
				// For the first valid direction update the frame and you don't
				// need to search anymore
				break;
			}
		}

		setResistance(this.material);
	}

	@Override
	protected void registerData() {
		this.getDataManager().register(FRAME_ID, 1);
		this.getDataManager().register(COLOR_RED, 255);
		this.getDataManager().register(COLOR_GREEN, 255);
		this.getDataManager().register(COLOR_BLUE, 255);
		this.getDataManager().register(BURNT, false);
	}

	@Override
	public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		if (player.canPlayerEdit(hangingPosition, this.facingDirection, itemstack)) {
			if (!itemstack.isEmpty()) {

				if (DecorConfig.COMMON.dyeFrames.get()) {
					DyeColor color = DyeColor.getColor(itemstack);
					if (color != null) {
						if (dyeFrame(color)) {
							if (!player.abilities.isCreativeMode) {
								itemstack.shrink(1);
							}
							return ActionResultType.SUCCESS;
						}
					}
				}

				if (itemstack.getItem() == this.material.getFrameItem()) {
					return updateFrame() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
				}
			}
		}
		return ActionResultType.FAIL;
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
				this.getDataManager().set(FRAME_ID, tryFrame.id);
				updateFacingWithBoundingBox(this.facingDirection);
				if (onValidSurface()) {
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

		if (!this.world.isRemote)
			playPlaceSound();

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

		this.getDataManager().set(COLOR_RED, newred);
		this.getDataManager().set(COLOR_GREEN, newgreen);
		this.getDataManager().set(COLOR_BLUE, newblue);
		this.getDataManager().set(BURNT, burn);

		if (!this.world.isRemote) {
			if (burn) {
				playBurnSound();
			} else {
				playPlaceSound();
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
	protected void updateBoundingBox() {
		if (this.facingDirection != null) {
			double x = (double) this.hangingPosition.getX() + 0.5D;
			double y = (double) this.hangingPosition.getY() + 0.5D;
			double z = (double) this.hangingPosition.getZ() + 0.5D;
			double widthOffset = this.offs(this.getWidthPixels());
			double heightOffset = this.offs(this.getHeightPixels());
			x = x - (double) this.facingDirection.getXOffset() * 0.46875D;
			z = z - (double) this.facingDirection.getZOffset() * 0.46875D;
			y = y + heightOffset;
			Direction enumfacing = this.facingDirection.rotateYCCW();
			x = x + widthOffset * (double) enumfacing.getXOffset();
			z = z + widthOffset * (double) enumfacing.getZOffset();
			this.setRawPosition(x, y, z);
			double width = (double) this.getWidthPixels();
			double height = (double) this.getHeightPixels();
			double depth = (double) this.getWidthPixels();

			if (this.facingDirection.getAxis() == Direction.Axis.Z) {
				depth = 1.0D;
			} else {
				width = 1.0D;
			}

			width = width / 32.0D;
			height = height / 32.0D;
			depth = depth / 32.0D;

			this.setBoundingBox(new AxisAlignedBB(x - width, y - height, z - depth, x + width, y + height, z + depth));

			if (this.facingDirection.getAxis() == Direction.Axis.Z) {
				width += 0.1F;
				height += 0.1F;
				depth = 1.0F;
			} else {
				width = 1.0F;
				height += 0.1F;
				depth += 0.1F;
			}
			this.fireboundingBox = new AxisAlignedBB(x - width, y - height, z - depth, x + width, y + height, z + depth);
		}

	}

	private double offs(int size) {
		return size % 32 == 0 ? 0.5D : 0.0D;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		BlockPos blockpos = this.hangingPosition.add(x - this.getPosX(), y - this.getPosY(), z - this.getPosZ());
		this.setPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
		this.setRotation(yaw, pitch);
	}

	@Override
	public void tick() {
		if (this.material == FrameMaterial.WOOD && DecorConfig.COMMON.burnFrames.get()) {
			if (this.world.func_234853_a_(this.fireboundingBox.expand(-0.001D, -0.001D, -0.001D)).anyMatch((state) -> state.getMaterial() == Material.FIRE) && !this.getBurned()) {
				dyeFrame(DyeColor.BLACK, true);
			}
		}
	}

	public FrameType getCurrentFrame() {
		return FrameType.getFrameById(this.getDataManager().get(FRAME_ID));
	}

	@Override
	public boolean func_241849_j(Entity ent) {
		return ent instanceof PlayerEntity && this.func_241845_aY();
	}

	@Override
	public boolean func_241845_aY() {
		return getCurrentFrame().isCollidable;
	}

	@Override
	public boolean canBeCollidedWith() {
		PlayerEntity player = AssortedDecor.proxy.getClientPlayer();
		if (player == null)
			return false;

		for (Hand hand : Hand.values()) {
			ItemStack handStack = player.getHeldItem(hand);

			if (!handStack.isEmpty()) {
				if (handStack.getItem() == this.material.getFrameItem() || this.material.effectiveTool().isInstance(handStack.getItem()) || DyeColor.getColor(handStack) != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float damage) {
		if (this.isInvulnerableTo(damagesource)) {
			return false;
		} else {
			if (!this.removed && !this.world.isRemote) {
				if (damagesource.getTrueSource() instanceof PlayerEntity) {
					this.remove();
					this.markVelocityChanged();
					this.onBroken(damagesource.getTrueSource());
					return true;
				}

				if (damage > this.resistance) {
					this.remove();
					this.markVelocityChanged();
					this.onBroken(damagesource.getTrueSource());
					return true;
				}
			}

			return false;
		}
	}

	public int getFrameID() {
		return this.getDataManager().get(FRAME_ID);
	}

	public int[] getFrameColor() {
		return new int[] { this.getDataManager().get(COLOR_RED), this.getDataManager().get(COLOR_GREEN), this.getDataManager().get(COLOR_BLUE) };
	}

	public boolean getBurned() {
		return this.getDataManager().get(BURNT);
	}

	public void playBurnSound() {
		this.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0F, 1.0F);
	}

	@Override
	public void writeAdditional(CompoundNBT nbttagcompound) {
		super.writeAdditional(nbttagcompound);
		nbttagcompound.putByte("Facing", (byte) this.facingDirection.getHorizontalIndex());
		nbttagcompound.putInt("Motive", this.getFrameID());
		int[] color = getFrameColor();
		nbttagcompound.putInt("Red", color[0]);
		nbttagcompound.putInt("Green", color[1]);
		nbttagcompound.putInt("Blue", color[2]);
		nbttagcompound.putInt("Material", this.material.ordinal());
		nbttagcompound.putBoolean("Burnt", getBurned());
	}

	@Override
	public void readAdditional(CompoundNBT nbttagcompound) {
		super.readAdditional(nbttagcompound);
		this.facingDirection = Direction.byHorizontalIndex(nbttagcompound.getByte("Facing"));
		this.getDataManager().set(COLOR_RED, nbttagcompound.getInt("Red"));
		this.getDataManager().set(COLOR_GREEN, nbttagcompound.getInt("Green"));
		this.getDataManager().set(COLOR_BLUE, nbttagcompound.getInt("Blue"));
		setResistance(this.material = FrameMaterial.values()[nbttagcompound.getInt("Material")]);
		this.getDataManager().set(FRAME_ID, nbttagcompound.getInt("Motive"));
		this.getDataManager().set(BURNT, nbttagcompound.getBoolean("Burnt"));
		this.updateFacingWithBoundingBox(this.facingDirection);
	}

	@Override
	public void writeSpawnData(PacketBuffer data) {
		data.writeInt(this.getFrameID());
		data.writeBlockPos(this.hangingPosition);
		data.writeEnumValue(this.material);
		data.writeInt(this.facingDirection.getHorizontalIndex());

		int[] color = getFrameColor();
		data.writeInt(color[0]);
		data.writeInt(color[1]);
		data.writeInt(color[2]);
		data.writeBoolean(this.getBurned());
	}

	@Override
	public void readSpawnData(PacketBuffer data) {
		this.getDataManager().set(FRAME_ID, data.readInt());

		this.hangingPosition = data.readBlockPos();
		this.material = data.readEnumValue(FrameMaterial.class);
		setResistance(this.material);
		updateFacingWithBoundingBox(Direction.byHorizontalIndex(data.readInt()));

		this.getDataManager().set(COLOR_RED, data.readInt());
		this.getDataManager().set(COLOR_GREEN, data.readInt());
		this.getDataManager().set(COLOR_BLUE, data.readInt());
		this.getDataManager().set(BURNT, data.readBoolean());
	}

	@Override
	public int getWidthPixels() {
		return this.getCurrentFrame().sizeX;
	}

	@Override
	public int getHeightPixels() {
		return this.getCurrentFrame().sizeY;
	}

	@Override
	public void onBroken(Entity brokenEntity) {
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.playBreakSound();
			if (brokenEntity instanceof PlayerEntity) {
				PlayerEntity playerentity = (PlayerEntity) brokenEntity;
				if (playerentity.abilities.isCreativeMode) {
					return;
				}
			}

			this.entityDropItem(this.material.getFrameItem());
		}
	}

	public void playBreakSound() {
		switch (this.material) {
		case WOOD:
			this.playSound(SoundEvents.BLOCK_WOOD_BREAK, 1.0F, 1.0F);
			break;
		case IRON:
			this.playSound(SoundEvents.BLOCK_METAL_BREAK, 1.0F, 1.0F);
		}
	}

	@Override
	public void playPlaceSound() {
		switch (this.material) {
		case WOOD:
			this.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1.0F, 1.0F);
			break;
		case IRON:
			this.playSound(SoundEvents.BLOCK_METAL_PLACE, 1.0F, 1.0F);
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
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