package ARLib.blockentities;

import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.IGuiHandler;
import ARLib.gui.modules.guiModuleEnergy;
import ARLib.multiblockCore.UniversalBattery;
import ARLib.network.INetworkTagReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;
import static ARLib.ARLibRegistry.ENTITY_ENERGY_OUTPUT_BLOCK;

public class EntityEnergyOutputBlock extends EntityEnergyInputBlock {


    public EntityEnergyOutputBlock(BlockPos p_155229_, BlockState p_155230_) {
        super(ENTITY_ENERGY_OUTPUT_BLOCK.get(),p_155229_, p_155230_);
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

}
