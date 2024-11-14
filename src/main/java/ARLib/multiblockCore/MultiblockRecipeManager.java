package ARLib.multiblockCore;

import ARLib.utils.MachineRecipe;

public class MultiblockRecipeManager<T extends EntityMultiblockMaster> {

    public boolean hasWork;
    public int progress;
    public MachineRecipe currentRecipe;
    T master;
    public MultiblockRecipeManager(T masterTile){
        this.master = masterTile;
    }

}
