package ARLib.blocks;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.network.BlockEntityPacket;
import ARLib.utils.DimensionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
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
        if (e instanceof EntityEnergyInputBlock tile){
            System.out.println(tile.getEnergyStored());
        }


        if (world.isClientSide()){
            PacketDistributor.sendToServer(new BlockEntityPacket(1, DimensionUtils.getLevelId(world),pos.getX(),pos.getY(),pos.getZ(),new byte[128]));
        }

        return InteractionResult.SUCCESS;
    }

}
