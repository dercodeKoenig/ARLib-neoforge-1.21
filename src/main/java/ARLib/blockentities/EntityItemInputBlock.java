package ARLib.blockentities;


import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.guiModuleItemSlot;
import ARLib.network.INetworkTagReceiver;
import ARLib.utils.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import static ARLib.ARLibRegistry.ENTITY_ITEM_INPUT_BLOCK;

// TODO this IITEMHANDLER should go into its own class
public class EntityItemInputBlock extends BlockEntity implements IItemHandler, INetworkTagReceiver {

    ItemStackHandler inventory;
    GuiHandlerBlockEntity guiHandler;

    public EntityItemInputBlock(BlockPos pos, BlockState blockState) {
        super(ENTITY_ITEM_INPUT_BLOCK.get(), pos, blockState);
        guiHandler = new GuiHandlerBlockEntity(this);
        this.guiHandler.registerModule(new guiModuleItemSlot(0,this, 0,this.guiHandler,20,20) );
        this.guiHandler.registerModule(new guiModuleItemSlot(1,this, 1,this.guiHandler,20,50) );
        this.guiHandler.registerModule(new guiModuleItemSlot(2,this, 2,this.guiHandler,50,20) );
        this.guiHandler.registerModule(new guiModuleItemSlot(3,this, 3,this.guiHandler,50,50) );

        inventory = new ItemStackHandler(4,this);
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
        return inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return inventory.isItemValid(slot,stack);
    }

    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {
        if(!level.isClientSide)
            GuiHandlerBlockEntity.serverTick(((EntityItemInputBlock)t).guiHandler);
    }
}
