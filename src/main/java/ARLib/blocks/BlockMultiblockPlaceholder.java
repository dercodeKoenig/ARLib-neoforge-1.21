package ARLib.blocks;

import ARLib.multiblockCore.BlockEntityMultiblockMaster;
import ARLib.multiblockCore.BlockMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockMultiblockPlaceholder extends BlockMultiblockPart {

    BlockState replacedBlock = null;

    public BlockMultiblockPlaceholder(Properties properties) {
        super(properties);
    }

    public void setReplacedBlock(BlockState replacedBlock){
        this.replacedBlock = replacedBlock;
    }
    public BlockState getReplacedBlock(){
        return replacedBlock;
    }

    // This method will drop the replaced block when the placeholder block is broken
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!world.isClientSide) {
            // Drop the replaced block as an item
            if (replacedBlock != null && willHarvest) {
                ItemStack stack = new ItemStack(replacedBlock.getBlock());
                popResource(world, pos, stack);
            }
        }
        return super.onDestroyedByPlayer(state,world,pos,player,willHarvest,fluid);
    }

}
