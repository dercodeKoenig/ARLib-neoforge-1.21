package ARLib.multiblockCore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

import static ARLib.multiblockCore.BlockMultiblockMaster.STATE_HIDE_BLOCK;

public class BlockMultiblockPart extends Block {

    private final Map<BlockPos, BlockPos> multiblockMasterPositions = new HashMap<>();

    public BlockMultiblockPart(Properties properties) {
        super(properties.noOcclusion().pushReaction(PushReaction.IGNORE));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).
                setValue(STATE_HIDE_BLOCK, false));

    }

    public void setMaster(BlockPos mypos, BlockPos masterpos) {
        if (masterpos == null && multiblockMasterPositions.containsKey(mypos))
            multiblockMasterPositions.remove(mypos);
        else
            multiblockMasterPositions.put(mypos, masterpos);
    }
    public BlockPos getMaster(BlockPos mypos){
        return multiblockMasterPositions.get(mypos);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING); // Define the FACING property
        builder.add(STATE_HIDE_BLOCK); // Define the state property
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(STATE_HIDE_BLOCK, false).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack) {
        world.setBlock(pos, state.setValue(STATE_HIDE_BLOCK, false).setValue(BlockStateProperties.HORIZONTAL_FACING, placer.getDirection().getOpposite()), 2);
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        System.out.println(level.getBlockState(pos));
        if (state.getBlock() instanceof BlockMultiblockPart t) {
            BlockPos master = t.getMaster(pos);
            if (master != null && level.getBlockEntity(master) instanceof EntityMultiblockMaster masterTile) {
                masterTile.scanStructure();
            }
            multiblockMasterPositions.remove(pos);
        }
    }
}
