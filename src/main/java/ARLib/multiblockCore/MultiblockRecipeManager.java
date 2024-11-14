package ARLib.multiblockCore;

import ARLib.utils.MachineRecipe;

import java.util.ArrayList;
import java.util.List;

public class MultiblockRecipeManager<T extends EntityMultiblockMaster> {

    public boolean hasWork;
    public int progress;
    public MachineRecipe currentRecipe;
    public List<MachineRecipe> recipes = new ArrayList<>();
    T master;
    public MultiblockRecipeManager(T masterTile){
        this.master = masterTile;
    }

    public void update(){
        for (MachineRecipe r : recipes){

            if(master.hasinputs(r.inputs) && master.canFitOutputs(r.outputs)){
                System.out.println("process recipe now!");
                master.consumeInput(r.inputs);
                master.produceOutput(r.outputs);
            }

        }
    }

}
