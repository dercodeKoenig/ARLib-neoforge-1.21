package ARLib.multiblockCore;

import ARLib.utils.ItemFluidStacks;
import ARLib.utils.MachineRecipe;
import ARLib.utils.recipePart;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static ARLib.utils.ItemUtils.getFluidStackFromId;
import static ARLib.utils.ItemUtils.getItemStackFromId;

public class MultiblockRecipeManager<T extends EntityMultiblockMaster> {


    public int progress;
    public MachineRecipe currentRecipe;
    public List<MachineRecipe> recipes = new ArrayList<>();
    public T master;

    public MultiblockRecipeManager(T masterTile) {
        this.master = masterTile;
    }

    public void reset() {
        currentRecipe = null;
        progress = 0;
    }

    public ItemFluidStacks getNextProducedItems(){
        ItemFluidStacks r = new ItemFluidStacks();
        if(currentRecipe != null){
            for (recipePart i:currentRecipe.outputs){
                ItemStack istack = getItemStackFromId(i.id, i.actual_num);
                FluidStack fstack = getFluidStackFromId(i.id, i.actual_num);
                if(istack!=null)
                    r.itemStacks.add(istack);
                if(fstack!=null)
                    r.fluidStacks.add(fstack);
            }
        }
        return r;
    }

    public void scanFornewRecipe() {
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
