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
 * A castle gate or garage door: a column hung from a solid ceiling that reaches down to the first
 * thing in its way, up to {@link #MAX_LENGTH} blocks. Opening it takes away every block but the
 * {@link #TOP} one, which stays retracted against the ceiling, so the doorway is really empty and
 * can be built in; closing it lets it down again as far as it can now reach. Columns side by side
 * with the same facing open and
 * close together, by the activator item or by a change in redstone power at their top blocks.
 */
public class GateBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    /** The block hanging from the ceiling, the only one an open gate keeps. */
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    /** How far a gate reaches down from its top, and how many columns open as one. */
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
        if (!level.isClientSide()) {
            this.fillBelow(level, pos);
        }
    }

    /** Where a closed gate can reach into: empty or replaceable, and not holding a fluid. */
    private static boolean canFill(BlockState state) {
        return state.canBeReplaced() && state.getFluidState().isEmpty();
    }

    /**
     * Lets a closed column down from {@code from} to the first thing in its way, never further than
     * {@link #MAX_LENGTH} below its top.
     */
    private void fillBelow(Level level, BlockPos from) {
        BlockState state = level.getBlockState(from);
        if (!state.is(this) || state.getValue(OPEN)) {
            return;
        }

        BlockPos top = this.topOf(level, from);
        BlockState lower = state.setValue(TOP, false);
        for (BlockPos p = from.below(); top.getY() - p.getY() <= MAX_LENGTH && canFill(level.getBlockState(p)); p = p.below()) {
            level.setBlock(p, lower, Block.UPDATE_ALL);
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
            this.setOpen(player, level, pos, !state.getValue(OPEN), state.getValue(POWERED));
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Redstone opens the gate while it powers the top block of any of its columns, and acts only on
     * a change. Only the tops count: they are the blocks an open gate keeps, so they always hear it.
     */
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) {
            return;
        }

        boolean powered = this.isPowered(level, pos);
        if (powered != state.getValue(POWERED)) {
            this.setOpen(null, level, pos, powered, powered);
        }
    }

    private boolean isPowered(Level level, BlockPos pos) {
        return this.connectedTops(level, pos).stream().anyMatch(level::hasNeighborSignal);
    }

    public void setOpen(@Nullable Player player, Level level, BlockPos pos, boolean open) {
        this.setOpen(player, level, pos, open, level.getBlockState(pos).getValue(POWERED));
    }

    /**
     * Opens or closes the gate at {@code pos} with every column joined to it. Opening lifts each
     * column into its top block; closing lets each one down as far as it now reaches.
     */
    public void setOpen(@Nullable Player player, Level level, BlockPos pos, boolean open, boolean powered) {
        List<BlockPos> tops = this.connectedTops(level, pos);

        // POWERED goes on every block first, quietly: the moves below notify the gate's own blocks,
        // and one still reading the old value would start this all over again.
        for (BlockPos top : tops) {
            for (BlockPos p = top; top.getY() - p.getY() <= MAX_LENGTH && level.getBlockState(p).is(this); p = p.below()) {
                level.setBlock(p, level.getBlockState(p).setValue(POWERED, powered), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            }
        }

        for (BlockPos top : tops) {
            if (open) {
                List<BlockPos> lower = new ArrayList<>();
                for (BlockPos p = top.below(); lower.size() < MAX_LENGTH && level.getBlockState(p).is(this); p = p.below()) {
                    lower.add(p);
                }
                // From the bottom up, so nothing is left hanging to break on its own.
                for (int i = lower.size() - 1; i >= 0; i--) {
                    level.setBlock(lower.get(i), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
                level.setBlock(top, level.getBlockState(top).setValue(OPEN, true), Block.UPDATE_ALL);
            } else {
                level.setBlock(top, level.getBlockState(top).setValue(OPEN, false), Block.UPDATE_ALL);
                this.fillBelow(level, top);
            }
        }
        this.playSound(player, level, pos, open);
    }

    private void playSound(@Nullable Player player, Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos, open ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    // ------------------------------------------------------------------ finding the gate

    /** The top of the column {@code pos} is in. */
    public BlockPos topOf(BlockGetter level, BlockPos pos) {
        BlockPos top = pos;
        for (int i = 0; i < MAX_LENGTH && level.getBlockState(top.above()).is(this); i++) {
            top = top.above();
        }
        return top;
    }

    /**
     * Everywhere the column under {@code top} is or could be: its blocks, then the space it would
     * close into, to the first thing in the way or {@link #MAX_LENGTH}.
     */
    public List<BlockPos> reach(BlockGetter level, BlockPos top) {
        List<BlockPos> reach = new ArrayList<>();
        reach.add(top);
        for (BlockPos p = top.below(); reach.size() <= MAX_LENGTH; p = p.below()) {
            BlockState state = level.getBlockState(p);
            if (!state.is(this) && !canFill(state)) {
                break;
            }
            reach.add(p);
        }
        return reach;
    }

    /**
     * The tops of every column joined to the one at {@code pos}: side by side anywhere along their
     * reach, the same gate, the same facing.
     */
    public List<BlockPos> connectedTops(BlockGetter level, BlockPos pos) {
        Direction facing = level.getBlockState(pos).getValue(FACING);
        Set<BlockPos> tops = new HashSet<>();
        List<BlockPos> ordered = new ArrayList<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(this.topOf(level, pos));

        while (!queue.isEmpty() && tops.size() < MAX_COLUMNS) {
            BlockPos top = queue.poll();
            if (!tops.add(top)) {
                continue;
            }
            ordered.add(top);

            for (BlockPos p : this.reach(level, top)) {
                for (Direction side : Direction.Plane.HORIZONTAL) {
                    BlockState neighbour = level.getBlockState(p.relative(side));
                    if (neighbour.is(this) && neighbour.getValue(FACING) == facing) {
                        queue.add(this.topOf(level, p.relative(side)));
                    }
                }
            }
        }
        return ordered;
    }
}
