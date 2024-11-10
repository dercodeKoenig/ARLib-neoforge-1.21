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
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
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


public class EntityItemInputBlock extends GuiCapableBlockEntity implements IItemHandler, Container {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(
            // The size of the list, i.e. the amount of slots in our container.
            4,
            // The default value to be used in place of where you'd use null in normal lists.
            ItemStack.EMPTY
    );
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag.getCompound("inventory"),inventory,registries);

    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        CompoundTag inv = new CompoundTag();
        ContainerHelper.saveAllItems(inv,inventory,registries);
        tag.put("inventory",inv);
    }

    public EntityItemInputBlock(BlockPos pos, BlockState blockState) {
        super(ENTITY_ITEM_INPUT_BLOCK.get(), pos, blockState);
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
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.inventory, slot, amount);
        this.setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(this.inventory, slot);
        this.setChanged();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        stack.limitSize(this.getMaxStackSize(stack));
        this.inventory.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        inventory.clear();
        this.setChanged();
    }

    @Override
    public int getSlots() {
        return inventory.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            if (slot < 0 || slot >= inventory.size()) {
                return stack;
            }

            if (!isItemValid(slot,stack)){
                return stack;
            }

            ItemStack existing = inventory.get(slot);

            int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));

            if (!existing.isEmpty()) {
                if (!existing.getItem().equals(stack.getItem()) || !existing.getTags().equals(stack.getTags())) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            }



            if (!simulate) {
                if (existing.isEmpty()) {
                    inventory.set(slot, new ItemStack(stack.getItem(), Math.min(limit,stack.getCount()) ));
                } else {
                    existing.grow(Math.min( limit, stack.getCount()));
                }
            }

        boolean reachedLimit = stack.getCount() >= limit;
            return reachedLimit ? new ItemStack(stack.getItem(), stack.getCount() - limit) : ItemStack.EMPTY;

    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        if (slot < 0 || slot >= inventory.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = inventory.get(slot);

        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());
        toExtract = Math.min(toExtract, existing.getCount());

        ItemStack extracted = new ItemStack(existing.getItem(), toExtract);

        if (!simulate) {
            if (existing.getCount() <= toExtract) {
                inventory.set(slot, ItemStack.EMPTY);
            } else {
                inventory.set(slot, new ItemStack(existing.getItem(), existing.getCount()- toExtract));
            }
        }

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
