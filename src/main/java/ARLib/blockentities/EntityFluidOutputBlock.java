package ARLib.blockentities;

import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.IGuiHandler;
import ARLib.gui.modules.guiModuleFluidTankDisplay;
import ARLib.network.INetworkTagReceiver;
import ARLib.utils.simpleOneTankFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import static ARLib.ARLibRegistry.ENTITY_FLUID_INPUT_BLOCK;
import static ARLib.ARLibRegistry.ENTITY_FLUID_OUTPUT_BLOCK;

public class EntityFluidOutputBlock extends EntityFluidInputBlock {
    public EntityFluidOutputBlock(BlockPos pos, BlockState blockState) {
        super(ENTITY_FLUID_OUTPUT_BLOCK.get(), pos, blockState);
    }
}
