package ARLib.blocks;

import ARLib.blockentities.EntityFluidInputBlock;
import ARLib.multiblockCore.BlockMultiblockPart;
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
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_FLUID_INPUT_BLOCK;
import static ARLib.ARLibRegistry.ENTITY_FLUID_OUTPUT_BLOCK;

public class BlockFluidOutputBlock extends BlockFluidInputBlock implements EntityBlock {

    public BlockFluidOutputBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ENTITY_FLUID_OUTPUT_BLOCK.get().create(pos, state);
    }

}
