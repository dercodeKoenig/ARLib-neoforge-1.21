package ARLib.multiblockCore;

import ARLib.network.PacketBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_PLACEHOLDER;

public class BlockMultiblockPlaceholder extends BlockMultiblockPart implements EntityBlock {
    //public final Map<BlockPos, BlockState> replacedStates = new HashMap<>();

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
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof EntityMultiblockPlaceholder tileE) {
                if (willHarvest) {
                    ItemStack stack = new ItemStack(tileE.replacedState.getBlock());
                    popResource(world, pos, stack);
                }
            }
        }
        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!world.isClientSide) {
            BlockPos master = getMaster(pos);
            if (master != null && world.getBlockEntity(master) instanceof EntityMultiblockMaster masterTile && masterTile.forwardInteractionToMaster) {
                return masterTile.useWithoutItem(state, world, pos, player, hitResult);
            }
        }
        return InteractionResult.PASS;
    }
}