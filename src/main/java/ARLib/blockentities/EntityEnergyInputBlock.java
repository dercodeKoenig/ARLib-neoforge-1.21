package ARLib.blockentities;

import ARLib.gui.IModularGui;
import ARLib.gui.guiModuleEnergy;
import ARLib.gui.guiModuleBase;
import ARLib.multiblockCore.UniversalBattery;
import ARLib.network.INetworkByteReceiver;
import ARLib.network.PacketBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static ARLib.ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK;

public class EntityEnergyInputBlock extends BlockEntity implements IEnergyStorage, INetworkByteReceiver, IModularGui {

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
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    void sendRequestDataUpdate(){
        //System.out.println("request update");
        CompoundTag tag = new CompoundTag();
        tag.putInt("requestId",0);
        PacketDistributor.sendToServer(PacketBlockEntity.getBlockEntityPacket(this, tag));
    }

    void sendDataUpdate(){

        CompoundTag tag = new CompoundTag();
        tag.putInt("energy_stored", getEnergyStored());
        tag.putInt("energy_capacity", getMaxEnergyStored());

        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                level.getChunkAt(getBlockPos()).getPos(),
                PacketBlockEntity.getBlockEntityPacket(this, tag)
        );
    }

    @Override
    public void readServer(CompoundTag tagIn) {
        //System.out.println(tagIn);
        if(tagIn.contains("requestId")){
            int id = tagIn.getInt("requestId");

            // send required data for gui
            if(id==0){
                sendDataUpdate();
            }
        }
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void readClient(CompoundTag tag) {
        //System.out.println(tag);
        if (tag.contains("energy_stored")){
            energyStorage.setEnergy(tag.getInt("energy_stored"));
        }
        if (tag.contains("energy_capacity")){
            energyStorage.setCapacity(tag.getInt("energy_capacity"));
        }
    }

    @Override
    public List<guiModuleBase> getModules() {
        List<guiModuleBase> modules = new ArrayList<>();
        modules.add(new guiModuleEnergy(this, 10,10));
        return  modules;
    }

    int last_gui_update = 0;
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onGuiTick() {
        last_gui_update+=1;
        if (last_gui_update > 10){
            last_gui_update = 0;
            sendRequestDataUpdate();
        }
    }
}
