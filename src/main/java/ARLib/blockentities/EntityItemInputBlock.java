package ARLib.blockentities;

import ARLib.gui.GuiCapableBlockEntity;
import ARLib.gui.guiModuleBase;
import ARLib.gui.guiModuleItemSlot;
import ARLib.network.PacketBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

import static ARLib.ARLibRegistry.ENTITY_ITEM_INPUT_BLOCK;


public class EntityItemInputBlock extends GuiCapableBlockEntity implements IItemHandler {

    SimpleContainer inventory;

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag lt = tag.getList("container", ListTag.TAG_LIST);
        inventory.fromTag(lt,registries);
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        ListTag lt = inventory.createTag(registries);
        tag.put("inventory",lt);
    }

    public EntityItemInputBlock(BlockPos pos, BlockState blockState) {
        super(ENTITY_ITEM_INPUT_BLOCK.get(), pos, blockState);
        this.registerModule(new guiModuleItemSlot(0,this, 0,this,20,20) );
        this.registerModule(new guiModuleItemSlot(1,this, 1,this,20,50) );
        this.registerModule(new guiModuleItemSlot(2,this, 2,this,50,20) );
        this.registerModule(new guiModuleItemSlot(3,this, 3,this,50,50) );

        inventory = new SimpleContainer(4);
    }


    @Override
    public void readServer(CompoundTag tagIn) {
        super.readServer(tagIn);
    }

    @Override
    public void readClient(CompoundTag tagIn) {
        super.readClient(tagIn);
    }



    @Override
    public int getSlots() {
        return inventory.getContainerSize();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            if (slot < 0 || slot >= inventory.getContainerSize()) {
                return stack;
            }

            if (!isItemValid(slot,stack)){
                return stack;
            }

            ItemStack existing = inventory.getItem(slot);

            int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));

            if (!existing.isEmpty()) {
                System.out.println(ItemStack.isSameItemSameComponents(existing,stack));


                if (!ItemStack.isSameItemSameComponents(existing,stack)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            }



            if (!simulate) {
                if (existing.isEmpty()) {
                    ItemStack newStack = stack.copy();
                    newStack. setCount(Math.min(limit,stack.getCount()));
                    inventory.setItem(slot, newStack);
                } else {
                    existing.grow(Math.min( limit, stack.getCount()));
                }
            }

            setChanged();

            boolean reachedLimit = stack.getCount() >= limit;
            return reachedLimit ? new ItemStack(stack.getItem(), stack.getCount() - limit) : ItemStack.EMPTY;

    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        if (slot < 0 || slot >= inventory.getContainerSize()) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = inventory.getItem(slot);

        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());
        toExtract = Math.min(toExtract, existing.getCount());

        ItemStack extracted = new ItemStack(existing.getItem(), toExtract);

        if (!simulate) {
            if (existing.getCount() <= toExtract) {
                inventory.setItem(slot, ItemStack.EMPTY);
            } else {
                inventory.setItem(slot, new ItemStack(existing.getItem(), existing.getCount()- toExtract));
            }
        }

        setChanged();
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {
        GuiCapableBlockEntity.tick(level,blockPos,blockState,(GuiCapableBlockEntity) t);
    }
}
