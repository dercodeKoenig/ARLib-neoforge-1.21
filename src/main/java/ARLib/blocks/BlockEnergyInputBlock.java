package ARLib.blocks;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.gui.IModularGui;
import ARLib.gui.modularScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;


public class BlockEnergyInputBlock extends Block  implements EntityBlock {
    public BlockEnergyInputBlock(Properties p_49795_) {
        super(p_49795_);
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ENTITY_ENERGY_INPUT_BLOCK.get().create(blockPos,blockState);
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity e = world.getBlockEntity(pos);
        if (e instanceof EntityEnergyInputBlock tile) {

            if (world.isClientSide) {
                Minecraft.getInstance().setScreen(new modularScreen((IModularGui) e));
            }

        }

        return InteractionResult.SUCCESS;
    }

}
