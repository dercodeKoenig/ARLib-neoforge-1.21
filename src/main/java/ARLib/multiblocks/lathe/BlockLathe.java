package ARLib.multiblocks.lathe;

import ARLib.multiblockCore.BlockMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static ARLib.multiblocks.MultiblockRegistry.ENTITY_LATHE;

public class BlockLathe extends BlockMultiblock {

    public BlockLathe(Properties properties) {
        super(properties);
    }




    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ENTITY_LATHE.get().create(blockPos,blockState);
    }

}
