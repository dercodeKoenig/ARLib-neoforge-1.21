package ARLib.blocks;

import ARLib.multiblockCore.BlockEntityMultiblockMaster;
import ARLib.multiblockCore.BlockMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMultiblockPlaceholder extends BlockMultiblockPart {

    BlockState replacedBlock = null;

    public BlockMultiblockPlaceholder(Properties properties) {
        super(properties);
    }

    public void setReplacedBlock(BlockState replacedBlock){
        this.replacedBlock = replacedBlock;
    }

    // This method will drop the replaced block when the placeholder block is broken
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!world.isClientSide) {
            // Drop the replaced block as an item
            if (replacedBlock != null) {
                ItemStack stack = new ItemStack(replacedBlock.getBlock());
                popResource(world, pos, stack);
            }

            if (super.getMasterBlockPos() != null && world.getBlockEntity(super.getMasterBlockPos()) instanceof BlockEntityMultiblockMaster master) {

            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

}
