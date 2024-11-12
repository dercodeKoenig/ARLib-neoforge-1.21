package ARLib.multiblockCore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class BlockMultiblockMaster extends Block implements EntityBlock {

    public static final BooleanProperty STATE_MULTIBLOCK_FORMED = BooleanProperty.create("state");

    public BlockMultiblockMaster(Properties p_49795_) {
        super(p_49795_.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(STATE_MULTIBLOCK_FORMED, false));
    }

    @Override
    public abstract @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState);


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING); // Define the FACING property
        builder.add(STATE_MULTIBLOCK_FORMED); // Define the state property
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(STATE_MULTIBLOCK_FORMED) ? 0 : 15; // Light passes through if multiblock is formed assuming it may not cover the entire block, fully blocks it otherwise
    }


    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(STATE_MULTIBLOCK_FORMED, false).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack) {
        world.setBlock(pos, state.setValue(STATE_MULTIBLOCK_FORMED, false).setValue(BlockStateProperties.HORIZONTAL_FACING, placer.getDirection().getOpposite()), 2);
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity e = world.getBlockEntity(pos);
        if (e instanceof BlockEntityMultiblockMaster){
            boolean res = ((BlockEntityMultiblockMaster) e).completeStructure();
            System.out.println(res);
        }
        return InteractionResult.SUCCESS;
    }
}
