package ARLib.multiblockCore;

import net.neoforged.neoforge.energy.EnergyStorage;

public class UniversalBattery extends EnergyStorage {

    public UniversalBattery(int capacity) {
        super(capacity);
    }

    public UniversalBattery(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public UniversalBattery(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public UniversalBattery(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }
    public void setEnergy(int e){
        energy = e;
    }
    public void setCapacity(int c){
        this.capacity = c;
    }
}
