package ARLib.multiblocks.lathe;

import ARLib.blockentities.EntityItemInputBlock;
import ARLib.multiblockCore.BlockMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_ITEM_INPUT_BLOCK;
import static ARLib.multiblocks.MultiblockRegistry.ENTITY_LATHE;

public class BlockLathe extends BlockMultiblock {

    public BlockLathe(Properties properties) {
        super(properties);
    }




    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ENTITY_LATHE.get().create(blockPos,blockState);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ENTITY_LATHE.get() ? EntityLathe::tick : null;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity e = world.getBlockEntity(pos);
        if (e instanceof EntityLathe ee) {
            if (world.isClientSide) {
                ee.guiHandler.openGui();
            }
        }
        return InteractionResult.SUCCESS;
    }

}
