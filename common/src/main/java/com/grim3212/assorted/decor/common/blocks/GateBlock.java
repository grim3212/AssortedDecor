package com.grim3212.assorted.decor.common.blocks;

import com.grim3212.assorted.decor.common.items.GateActivatorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A castle gate or garage door: a column hung from a solid ceiling that, once placed, fills down to
 * the floor. Opening it retracts every block but the top into it, and columns side by side with the
 * same facing open and close together. Its activator item, or a change in redstone power, opens and
 * closes it.
 */
public class GateBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    /** The block hanging from the ceiling, the one that stays visible while the gate is open. */
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    /** How far a placed gate fills down, and how many columns open as one. */
    public static final int MAX_LENGTH = 64;
    public static final int MAX_COLUMNS = 64;

    private final Supplier<? extends Item> activator;
    private final SoundEvent openSound;
    private final SoundEvent closeSound;

    public GateBlock(Properties properties, Supplier<? extends Item> activator, SoundEvent openSound, SoundEvent closeSound) {
        super(properties.pushReaction(PushReaction.BLOCK));
        this.activator = activator;
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(POWERED, false).setValue(TOP, true));
    }

    public Item getActivator() {
        return this.activator.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, TOP);
    }

    // ------------------------------------------------------------------ shape

    /** A two pixel plane across the block, a pixel in from the {@code facing} side. */
    private static VoxelShape plane(Direction facing, double minY) {
        return switch (facing) {
            case EAST -> Block.box(12, minY, 0, 14, 16, 16);
            case WEST -> Block.box(2, minY, 0, 4, 16, 16);
            case SOUTH -> Block.box(0, minY, 12, 16, 16, 14);
            default -> Block.box(0, minY, 2, 16, 16, 4);
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(OPEN)) {
            return plane(state.getValue(FACING), 0);
        }
        return state.getValue(TOP) ? plane(state.getValue(FACING), 7) : Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    // ------------------------------------------------------------------ placing and breaking

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState above = context.getLevel().getBlockState(context.getClickedPos().above());
        BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(TOP, !above.is(this));
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    /** Hangs from a sturdy ceiling, or from more of the same gate. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(this) || above.isFaceSturdy(level, pos.above(), Direction.DOWN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }

        BlockState lower = state.setValue(TOP, false);
        for (int i = 1; i <= MAX_LENGTH; i++) {
            BlockPos below = pos.below(i);
            BlockState there = level.getBlockState(below);
            if (!there.canBeReplaced() || !there.getFluidState().isEmpty()) {
                break;
            }
            level.setBlock(below, lower, Block.UPDATE_ALL);
        }
    }

    /**
     * Losing what it hangs from breaks a block, and {@code updateOrDestroy} drops what its loot
     * table allows, which is nothing below the top. That runs down the column on its own.
     */
    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction != Direction.UP) {
            return state;
        }
        if (!this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state.setValue(TOP, !neighborState.is(this));
    }

    /** Breaking any block breaks the column from its top, so the one item it came from drops. */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !state.getValue(TOP)) {
            level.destroyBlock(this.topOf(level, pos), !player.preventsBlockDrops(), player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    // ------------------------------------------------------------------ opening

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(this.getActivator())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        // Vanilla only stops an item's own use while it cools down, not a block's use of it.
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            if (stack.getItem() instanceof GateActivatorItem activator) {
                activator.sound(level, player, stack);
            }
            this.setOpen(player, level, pos, !state.getValue(OPEN));
        }
        return InteractionResult.SUCCESS;
    }

    /** Redstone opens the gate while any block of it is powered, and only acts on a change. */
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) {
            return;
        }

        List<BlockPos> gate = this.connected(level, pos);
        boolean powered = gate.stream().anyMatch(level::hasNeighborSignal);
        if (powered == state.getValue(POWERED)) {
            return;
        }

        for (BlockPos p : gate) {
            BlockState s = level.getBlockState(p);
            level.setBlock(p, s.setValue(POWERED, powered).setValue(OPEN, powered), Block.UPDATE_CLIENTS);
        }
        this.playSound(null, level, pos, powered);
    }

    /** Opens or closes the gate at {@code pos}, with every column joined to it. */
    public void setOpen(@Nullable Player player, Level level, BlockPos pos, boolean open) {
        for (BlockPos p : this.connected(level, pos)) {
            BlockState s = level.getBlockState(p);
            if (s.getValue(OPEN) != open) {
                level.setBlock(p, s.setValue(OPEN, open), Block.UPDATE_CLIENTS);
            }
        }
        this.playSound(player, level, pos, open);
    }

    private void playSound(@Nullable Player player, Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos, open ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    /** The top of the column {@code pos} is in. */
    public BlockPos topOf(BlockGetter level, BlockPos pos) {
        BlockPos top = pos;
        for (int i = 0; i < MAX_LENGTH && level.getBlockState(top.above()).is(this); i++) {
            top = top.above();
        }
        return top;
    }

    /**
     * Every block of every column joined to the one at {@code pos}: side by side, the same gate, the
     * same facing. Found a column at a time from each column's top.
     */
    public List<BlockPos> connected(BlockGetter level, BlockPos pos) {
        Direction facing = level.getBlockState(pos).getValue(FACING);
        List<BlockPos> blocks = new ArrayList<>();
        Set<BlockPos> tops = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(this.topOf(level, pos));

        while (!queue.isEmpty() && tops.size() < MAX_COLUMNS) {
            BlockPos top = queue.poll();
            if (!tops.add(top)) {
                continue;
            }

            BlockPos p = top;
            for (int i = 0; i < MAX_LENGTH && level.getBlockState(p).is(this); i++, p = p.below()) {
                blocks.add(p);
                for (Direction side : Direction.Plane.HORIZONTAL) {
                    BlockState neighbour = level.getBlockState(p.relative(side));
                    if (neighbour.is(this) && neighbour.getValue(FACING) == facing) {
                        queue.add(this.topOf(level, p.relative(side)));
                    }
                }
            }
        }
        return blocks;
    }
}
