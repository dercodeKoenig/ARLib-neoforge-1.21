package ARLib.multiblockCore;

import ARLib.network.PacketBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static ARLib.ARLibRegistry.ENTITY_PLACEHOLDER;

public class BlockMultiblockPlaceholder extends BlockMultiblockPart implements EntityBlock {
    public final Map<BlockPos, BlockState> replacedStates = new HashMap<>();

    public BlockMultiblockPlaceholder(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ENTITY_PLACEHOLDER.get().create(blockPos, blockState);
    }

    // This method will drop the replaced block when the placeholder block is broken
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!world.isClientSide) {
            if (replacedStates.containsKey(pos) && replacedStates.get(pos) != null && willHarvest) {
                ItemStack stack = new ItemStack(replacedStates.get(pos).getBlock());
                popResource(world, pos, stack);
            }
        }
        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        replacedStates.remove(pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!world.isClientSide) {
            CompoundTag info = new CompoundTag();
            BlockPos master = getMaster(pos);
            if (master != null && world.getBlockEntity(master) instanceof EntityMultiblockMaster masterTile && masterTile.alwaysOpenMasterGui) {
                info.putByte("openGui", (byte) 0);
                PacketDistributor.sendToPlayer((ServerPlayer) player, PacketBlockEntity.getBlockEntityPacket(world, master, info));
            }
        }
        return InteractionResult.SUCCESS;
    }
}