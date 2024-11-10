package ARLib.blockentities;


import ARLib.gui.GuiHandler;
import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.guiModuleItemHandlerSlot;
import ARLib.gui.guiModulePlayerInventorySlot;
import ARLib.network.INetworkTagReceiver;
import ARLib.utils.BlockEntityItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

import static ARLib.ARLibRegistry.ENTITY_ITEM_INPUT_BLOCK;

// TODO this IITEMHANDLER should go into its own class
public class EntityItemInputBlock extends BlockEntity implements IItemHandler, INetworkTagReceiver {

    BlockEntityItemStackHandler inventory;
    GuiHandler guiHandler;

    public EntityItemInputBlock(BlockPos pos, BlockState blockState) {
        super(ENTITY_ITEM_INPUT_BLOCK.get(), pos, blockState);


        guiHandler = new GuiHandlerBlockEntity(this);

        int containergroup = 0;
        int playerinventorygroup = 1;
        this.guiHandler.registerModule(new guiModuleItemHandlerSlot(0,this, 0,containergroup,playerinventorygroup,this.guiHandler,20,20) );
        this.guiHandler.registerModule(new guiModuleItemHandlerSlot(1,this, 1,containergroup,playerinventorygroup,this.guiHandler,20,50) );
        this.guiHandler.registerModule(new guiModuleItemHandlerSlot(2,this, 2,containergroup,playerinventorygroup,this.guiHandler,50,20) );
        this.guiHandler.registerModule(new guiModuleItemHandlerSlot(3,this, 3,containergroup,playerinventorygroup,this.guiHandler,50,50) );

        List<guiModulePlayerInventorySlot> playerHotBar =  guiModulePlayerInventorySlot.makePlayerHotbarModules(7,140,100,playerinventorygroup,containergroup,this.guiHandler);
        for (guiModulePlayerInventorySlot i:playerHotBar){
            this.guiHandler.registerModule(i);
        }

        List<guiModulePlayerInventorySlot> playerInv =  guiModulePlayerInventorySlot.makePlayerInventoryModules(7,80,200,playerinventorygroup,containergroup,this.guiHandler);
        for (guiModulePlayerInventorySlot i:playerInv){
            this.guiHandler.registerModule(i);
        }


        inventory = new BlockEntityItemStackHandler(4,this);
    }


    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        CompoundTag inv = inventory.serializeNBT(registries);
        tag.put("inventory",inv);
    }


    @Override
    public void readServer(CompoundTag tagIn) {
        this.guiHandler.readServer(tagIn);
    }

    @Override
    public void readClient(CompoundTag tagIn) {
        this.guiHandler.readClient(tagIn);
    }

    public void openGui(){
        guiHandler.openGui();
    }


    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return inventory.insertItem(slot,stack,simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inventory.extractItem(slot,amount,simulate);
    }

        @Override
    public int getSlotLimit(int slot) {
        return 99;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return inventory.isItemValid(slot,stack);
    }

    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {
        if(!level.isClientSide)
            GuiHandler.serverTick(((EntityItemInputBlock)t).guiHandler);
    }
}
