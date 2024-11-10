package ARLib.blockentities;

import ARLib.gui.GuiCapableBlockEntity;
import ARLib.gui.guiModuleEnergy;
import ARLib.gui.guiModuleBase;
import ARLib.multiblockCore.UniversalBattery;
import ARLib.network.INetworkByteReceiver;
import ARLib.network.PacketBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;

public class EntityEnergyInputBlock extends GuiCapableBlockEntity implements IEnergyStorage, INetworkByteReceiver {

    protected UniversalBattery energyStorage;


    public EntityEnergyInputBlock(BlockPos p_155229_, BlockState p_155230_) {
        super(ENTITY_ENERGY_INPUT_BLOCK.get(), p_155229_, p_155230_);
        energyStorage = new UniversalBattery(10000);
        this.registerModule(new guiModuleEnergy(0,this,this,10,10));
    }


    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(registries,tag.get("Energy"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
    }

    @Override
    public int receiveEnergy(int i, boolean b) {
        return energyStorage.receiveEnergy(i,b);
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        return energyStorage.extractEnergy(i,b);
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }


    @Override
    public void readServer(CompoundTag tagIn) {
      super.readServer(tagIn);
    }
    @Override
    public void readClient(CompoundTag tagIn) {
        super.readClient(tagIn);
    }

    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {
        GuiCapableBlockEntity.tick(level,blockPos,blockState,(GuiCapableBlockEntity) t);

        ((EntityEnergyInputBlock)t).extractEnergy(100,false);
    }

}
