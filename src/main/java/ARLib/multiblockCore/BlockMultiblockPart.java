package ARLib.multiblockCore;

import ARLib.blocks.BlockEnergyInputBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;

import static ARLib.multiblockCore.BlockMultiblockMaster.STATE_MULTIBLOCK_FORMED;

public class BlockMultiblockPart extends Block {


    BlockPos masterBlockPos = null;

    public BlockMultiblockPart(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE_MULTIBLOCK_FORMED, false));

    }

    public BlockPos getMasterBlockPos() {
        return masterBlockPos;
    }
    public void setMasterBlockPos(BlockState state, BlockPos pos) {
        this.masterBlockPos = pos;
        if (pos != null){
            state.setValue(STATE_MULTIBLOCK_FORMED,true);
        }
        else{
            state.setValue(STATE_MULTIBLOCK_FORMED,false);
        }
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        if (state.getBlock() instanceof BlockMultiblockPart bb){
            if(bb.masterBlockPos != null){
                // if a masterblock is assigned it is part of a multiblock structure so no light blocking
                // light blocking can cause the structure to have wrong light
                return 0;
            }
        }
        return 15;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE_MULTIBLOCK_FORMED); // Define the state property
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(STATE_MULTIBLOCK_FORMED, false);
    }
}
