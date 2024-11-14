package ARLib.multiblocks.lathe;

import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.IGuiHandler;
import ARLib.multiblockCore.EntityMultiblockMaster;
import ARLib.multiblockCore.MultiblockRecipeManager;
import ARLib.utils.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import static ARLib.ARLibRegistry.BLOCK_MOTOR;
import static ARLib.ARLibRegistry.BLOCK_STRUCTURE;
import static ARLib.multiblocks.MultiblockRegistry.BLOCK_LATHE;
import static ARLib.multiblocks.MultiblockRegistry.ENTITY_LATHE;


public class EntityLathe extends EntityMultiblockMaster {


    static List<MachineRecipe> recipes = new ArrayList<>();
    public static void addRecipe(MachineRecipe recipe){
        recipes.add(recipe);
    }

    IGuiHandler guiHandler;
    MultiblockRecipeManager<EntityLathe> recipeManager = new MultiblockRecipeManager<>(this);

    public EntityLathe(BlockPos pos, BlockState state) {
        super(ENTITY_LATHE.get(), pos, state);
        guiHandler = new GuiHandlerBlockEntity(this);
        //this.alwaysOpenMasterGui = true;
        recipeManager.recipes = EntityLathe.recipes;
    }

    @Override
    public void setupCharmappings() {
        super.setupCharmappings();
        List<Block> c = new ArrayList<>();
        c.add(BLOCK_LATHE.get());
        setMapping('c', c);
    }

    public static final Object[][][] structure = {
            {{'c',BLOCK_MOTOR.get(), Blocks.AIR,'I'}},
            {{'P', BLOCK_STRUCTURE.get(),BLOCK_STRUCTURE.get(),'O'}},
    };

    @Override
    public Object[][][] getStructure() {
        return structure;
    }


    public void openGui() {
        guiHandler.openGui(176, 126);
    }

    @Override
    public void readServer(CompoundTag tag) {
        guiHandler.readServer(tag);
        super.readServer(tag);
    }

    @Override
    public void readClient(CompoundTag tag) {
        guiHandler.readClient(tag);
        if(tag.contains("openGui")){
            openGui();
        }
        super.readClient(tag);
    }

    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {
        if (!level.isClientSide) {
            IGuiHandler.serverTick(((EntityLathe) t).guiHandler);

            if (((EntityLathe) t).isMultiblockFormed()) {
                ((EntityLathe) t).recipeManager.update();

            }
        }
    }
}
