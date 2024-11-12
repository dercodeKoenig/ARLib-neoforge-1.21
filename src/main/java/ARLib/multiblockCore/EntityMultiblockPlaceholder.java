package ARLib.multiblockCore;

import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.IGuiHandler;
import ARLib.gui.modules.guiModuleEnergy;
import ARLib.network.INetworkTagReceiver;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;
import static ARLib.ARLibRegistry.ENTITY_PLACEHOLDER;

public class EntityMultiblockPlaceholder extends BlockEntity {

    BlockState replacedState;

    public EntityMultiblockPlaceholder(BlockPos p_155229_, BlockState p_155230_) {
        this(ENTITY_PLACEHOLDER.get(), p_155229_, p_155230_);
    }

    public EntityMultiblockPlaceholder(BlockEntityType type, BlockPos p_155229_, BlockState p_155230_) {
        super(type, p_155229_, p_155230_);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(!level.isClientSide) {
            Block p = level.getBlockState(getBlockPos()).getBlock();
            if (p instanceof BlockMultiblockPlaceholder pp) {
                pp.replacedStates.put(getBlockPos(), replacedState);
                System.out.println("set replaced state for " + getBlockPos() + " to " + replacedState);
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("BlockState")) {
            CompoundTag blockStateNbt = tag.getCompound("BlockState");
            DataResult<BlockState> decodedBlockState = BlockState.CODEC.parse(NbtOps.INSTANCE, blockStateNbt);
            replacedState = decodedBlockState.getOrThrow();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (replacedState != null) {
            DataResult<CompoundTag> encodedBlockState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, replacedState)
                    .map(nbtTag -> (CompoundTag) nbtTag);
            tag.put("BlockState", encodedBlockState.getOrThrow());
        }
    }
}
