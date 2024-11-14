package ARLib.blocks;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.multiblockCore.EntityMultiblockMaster;
import ARLib.multiblockCore.BlockMultiblockPart;
import ARLib.network.PacketBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;


public class BlockEnergyInputBlock extends BlockMultiblockPart implements EntityBlock {
    public BlockEnergyInputBlock(Properties p_49795_) {
        super(p_49795_);
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ENTITY_ENERGY_INPUT_BLOCK.get().create(blockPos,blockState);
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!world.isClientSide) {
            CompoundTag info = new CompoundTag();
            BlockPos master = getMaster(pos);
            if (master != null && world.getBlockEntity(master) instanceof EntityMultiblockMaster masterTile && masterTile.alwaysOpenMasterGui) {
                info.putByte("openGui", (byte) 0);
                PacketDistributor.sendToPlayer((ServerPlayer) player, PacketBlockEntity.getBlockEntityPacket(world, master, info));
            } else {
                info.putByte("openGui", (byte) 0);
                PacketDistributor.sendToPlayer((ServerPlayer) player, PacketBlockEntity.getBlockEntityPacket(world, pos, info));

            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return EntityEnergyInputBlock::tick;
    }


}
