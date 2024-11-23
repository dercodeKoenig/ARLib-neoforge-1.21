package ARLib.multiblockCore;

import ARLib.utils.MachineRecipe;

import java.util.ArrayList;
import java.util.List;

public class MultiblockRecipeManager<T extends EntityMultiblockMaster> {


    public int progress;
    public MachineRecipe currentRecipe;
    public List<MachineRecipe> recipes = new ArrayList<>();
    T master;

    public MultiblockRecipeManager(T masterTile) {
        this.master = masterTile;
    }

    void reset() {
        currentRecipe = null;
        progress = 0;
    }

    void scanFornewRecipe() {
        for (MachineRecipe r : recipes) {
            if (master.hasinputs(r.inputs) && master.canFitOutputs(r.outputs)) {
                currentRecipe = r.copy(); // make a copy because they can have different actual_num values for every new recipe
                currentRecipe.compute_actual_output_nums(); // roll the dice to compute input / output to consume for given probability
                break;
            }
        }
    }

    // returns true if it was a processing tick, false if not. can be used to check if the machine is running
    public boolean  update() {
        if (currentRecipe == null) {
            scanFornewRecipe();
            return false;
        }
        if (master.hasinputs(currentRecipe.inputs) && master.canFitOutputs(currentRecipe.outputs)) {
            if (master.getTotalEnergyStored() >= currentRecipe.energyPerTick) {
                progress += 1;
                master.consumeEnergy(currentRecipe.energyPerTick);
                if (progress == currentRecipe.ticksRequired) {
                    master.consumeInput(currentRecipe.inputs, false);
                    master.produceOutput(currentRecipe.outputs);
                    reset();
                }
                return true;
            }
        } else {
            reset();
        }
        return false;
    }
}
