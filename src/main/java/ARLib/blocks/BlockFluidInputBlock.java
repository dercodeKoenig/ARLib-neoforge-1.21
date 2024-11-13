package ARLib.blocks;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.blockentities.EntityFluidInputBlock;
import ARLib.multiblockCore.BlockMultiblockPart;
import ARLib.utils.simpleOneTankFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_FLUID_INPUT_BLOCK;

public class BlockFluidInputBlock extends BlockMultiblockPart implements EntityBlock {

    public BlockFluidInputBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ENTITY_FLUID_INPUT_BLOCK.get().create(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity e = world.getBlockEntity(pos);
        if (e instanceof EntityFluidInputBlock ee) {
            if (world.isClientSide) {
                ee.openGui();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return EntityFluidInputBlock::tick;
    }

}
