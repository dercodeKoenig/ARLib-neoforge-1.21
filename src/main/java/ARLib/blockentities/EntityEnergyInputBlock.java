package ARLib.blockentities;

import ARLib.multiblockCore.UniversalBattery;
import ARLib.network.INetworkByteReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;

public class EntityEnergyInputBlock extends BlockEntity implements IEnergyStorage, INetworkByteReceiver {

    protected UniversalBattery energyStorage;


    public EntityEnergyInputBlock(BlockPos p_155229_, BlockState p_155230_) {
        super(ENTITY_ENERGY_INPUT_BLOCK.get(), p_155229_, p_155230_);
        energyStorage = new UniversalBattery(10000);
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
        return energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energyStorage.canReceive();
    }

    @Override
    public void read_bytes(int packetid, byte[] bytes) {
        System.out.println("incoming data:"+level.isClientSide()+":"+packetid);
    }

}
