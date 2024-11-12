package ARLib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;
import static ARLib.ARLibRegistry.ENTITY_ENERGY_OUTPUT_BLOCK;

public class BlockEnergyOutputBlock extends BlockEnergyInputBlock{
    public BlockEnergyOutputBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ENTITY_ENERGY_OUTPUT_BLOCK.get().create(blockPos,blockState);
    }
}
