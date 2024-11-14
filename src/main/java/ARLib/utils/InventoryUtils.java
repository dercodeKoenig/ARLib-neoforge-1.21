package ARLib.utils;

import ARLib.blockentities.EntityFluidInputBlock;
import ARLib.blockentities.EntityItemInputBlock;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ARLib.utils.ItemUtils.*;

public class InventoryUtils {
    public static   <I extends IItemHandler, F extends IFluidHandler> boolean canFitElements(List<I> itemInTiles, List<F> fluidInTiles, Map<String, Integer> elements) {
        List<ItemStack> itemStacks = new ArrayList<>();
        List<FluidStack> fluidStacks = new ArrayList<>();

        for (String id : elements.keySet()) {
            int num = elements.get(id);
            ItemStack istack = getItemStackFromId(id, num);
            if (istack != null) {
                itemStacks.add(istack);
            }
            FluidStack fstack = getFluidStackFromId(id, num);
            if (fstack != null) {
                fluidStacks.add(fstack);
            }
        }

        return (canInsertAllItems(itemInTiles, itemStacks) && canInsertAllFluids(fluidInTiles, fluidStacks));
    }

    public static  <I extends IItemHandler> boolean canInsertAllItems(List<I> itemHandlers, List<ItemStack> itemsToInsert) {
        // Create a list of temporary simulated slots representing all slots in all handlers
        List<ItemStack> simulatedSlots = new ArrayList<>();

        // Initialize the simulated slots with copies of each slot in all handlers
        for (IItemHandler handler : itemHandlers) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack slotCopy = handler.getStackInSlot(slot).copy();  // Copy to prevent modification of real slots
                simulatedSlots.add(slotCopy);
            }
        }

        // For each item that we want to insert
        for (ItemStack stackToInsert : itemsToInsert) {
            if (stackToInsert.isEmpty()) continue;  // Skip empty items

            // Track the remaining count of the stack that still needs to be inserted
            int remainingCount = stackToInsert.getCount();

            // Try to insert into the simulated slots
            for (int i = 0; i < simulatedSlots.size(); i++) {
                if (remainingCount <= 0) break;  // If the item is fully inserted, move to the next item

                // Check if the current simulated slot can accept this item
                if (simulatedSlots.get(i).isEmpty() || (ItemStack.isSameItemSameComponents(simulatedSlots.get(i), stackToInsert))) {
                    int spaceAvailable = simulatedSlots.get(i).getMaxStackSize() - simulatedSlots.get(i).getCount();
                    int toInsert = Math.min(spaceAvailable, remainingCount);

                    // Simulate insertion by "adding" items to the simulated slot
                    if (simulatedSlots.get(i).isEmpty()) {
                        // If the slot is empty, simulate creating a new stack in this slot
                        simulatedSlots.set(i, stackToInsert.copy());
                    } else {
                        // If the slot already contains compatible items, add to the existing count
                        simulatedSlots.get(i).grow(toInsert);
                    }

                    // Reduce the remaining count by the amount that was able to be inserted
                    remainingCount -= toInsert;
                }
            }

            // If there's remaining count after attempting to insert into all slots, return false
            if (remainingCount > 0) {
                return false;
            }
        }

        // If all items were fully inserted in the simulation, return true
        return true;
    }


    public static <F extends IFluidHandler> boolean canInsertAllFluids(List<F> fluidHandlers, List<FluidStack> fluidsToInsert) {
        // Create a list of simulated tanks representing all tanks in all handlers
        List<FluidStack> simulatedTanks = new ArrayList<>();
        // Create a list of corresponding capacities for each simulated tank
        List<Integer> capacities = new ArrayList<>();

        // Initialize the simulated tanks and store their corresponding maximum capacities
        for (IFluidHandler handler : fluidHandlers) {
            for (int tankIndex = 0; tankIndex < handler.getTanks(); tankIndex++) {
                // Copy the current fluid stack in the tank to prevent modification of the real tank
                FluidStack tankCopy = handler.getFluidInTank(tankIndex).copy();
                simulatedTanks.add(tankCopy);

                // Store the maximum capacity of the current tank
                int maxCapacity = handler.getTankCapacity(tankIndex);
                capacities.add(maxCapacity);
            }
        }

        // For each fluid that we want to insert
        for (FluidStack fluidToInsert : fluidsToInsert) {
            if (fluidToInsert.isEmpty()) continue;  // Skip empty fluids

            // Track the remaining amount of the fluid that still needs to be inserted
            int remainingAmount = fluidToInsert.getAmount();

            // Try to insert into the simulated tanks using a single index for both the tanks and their capacities
            for (int i = 0; i < simulatedTanks.size(); i++) {
                if (remainingAmount <= 0) break;  // If the fluid is fully inserted, move to the next fluid
                int maxCapacity = capacities.get(i);  // Get the corresponding max capacity for this simulated tank

                // Check if the current simulated tank can accept this fluid
                if (simulatedTanks.get(i).isEmpty() || FluidStack.isSameFluid(simulatedTanks.get(i),fluidToInsert)) {
                    int spaceAvailable = maxCapacity - simulatedTanks.get(i).getAmount(); // Calculate available space in the tank
                    int toInsert = Math.min(spaceAvailable, remainingAmount);

                    // Simulate insertion by "adding" fluid to the simulated tank
                    if (simulatedTanks.get(i).isEmpty()) {
                        // If the tank is empty, simulate filling it with this fluid
                        simulatedTanks.set(i, fluidToInsert.copy());
                    } else {
                        // If the tank already contains compatible fluids, add to the existing amount
                        simulatedTanks.get(i).grow(toInsert);
                    }

                    // Reduce the remaining amount by the amount that was able to be inserted
                    remainingAmount -= toInsert;
                }
            }

            // If there's remaining fluid after attempting to insert into all tanks, return false
            if (remainingAmount > 0) {
                return false;
            }
        }

        // If all fluids were fully inserted in the simulation, return true
        return true;
    }


    public static <F extends IFluidHandler, I extends IItemHandler> void createElements(List<F> fluidHandlers, List<I> itemHandlers, String id_or_tag_to_consume, int num) {
        ItemStack istack = getItemStackFromId(id_or_tag_to_consume, num);
        if(istack != null){
            for (int i = 0; i < itemHandlers.size(); i++) {
                for (int o = 0; o < itemHandlers.get(i).getSlots(); o++) {
                    istack = itemHandlers.get(i).insertItem(o, istack, false);
                    if(istack.isEmpty())return;
                }
            }
        }
        FluidStack fstack = getFluidStackFromId(id_or_tag_to_consume, num);
        if(fstack != null){
            for (int i = 0; i < fluidHandlers.size(); i++) {
                int filled = fluidHandlers.get(i).fill(fstack, IFluidHandler.FluidAction.EXECUTE);
                fstack.shrink(filled);
                if(fstack.isEmpty())return;
            }
        }
    }


public static <F extends IFluidHandler, I extends IItemHandler> void consumeElements(List<F> fluidHandlers, List<I> itemHandlers, String id_or_tag_to_consume, int num) {

    for (int i = 0; i < itemHandlers.size(); i++) {
        for (int o = 0; o < itemHandlers.get(i).getSlots(); o++) {
            if (!itemHandlers.get(i).getStackInSlot(o).isEmpty()) {
                if (matches(id_or_tag_to_consume, itemHandlers.get(i).getStackInSlot(o))) {
                    ItemStack extracted = itemHandlers.get(i).extractItem(o, num, false);
                    num -= extracted.getCount();
                    if (num == 0)
                        return;
                }
            }
        }
    }

    for (int i = 0; i < fluidHandlers.size(); i++) {
        for (int o = 0; o < fluidHandlers.get(i).getTanks(); o++) {
            if (!fluidHandlers.get(i).getFluidInTank(o).isEmpty()) {
                if (matches(id_or_tag_to_consume, fluidHandlers.get(i).getFluidInTank(o))) {
                    FluidStack drained = fluidHandlers.get(i).drain(fluidHandlers.get(i).getFluidInTank(o).copyWithAmount(num), IFluidHandler.FluidAction.EXECUTE);
                    num -= drained.getAmount();
                    if (num == 0)
                        return;
                }
            }
        }
    }
}



    public static <F extends IFluidHandler, I extends IItemHandler> boolean hasInputs(List<I> itemInTiles, List<F> fluidInTiles, Map<String, Integer> inputs) {
        // Collect all non-empty item stacks from the item handlers
        List<ItemStack> myInputItems = new ArrayList<>();
        for (IItemHandler handler : itemInTiles) {
            for (int o = 0; o < handler.getSlots(); o++) {
                ItemStack s = handler.getStackInSlot(o);
                if (!s.isEmpty()) {
                    myInputItems.add(s.copy());
                }
            }
        }

        // Collect all non-empty fluid stacks from the fluid handlers
        List<FluidStack> myInputFluids = new ArrayList<>();
        for (IFluidHandler handler : fluidInTiles) {
            for (int o = 0; o < handler.getTanks(); o++) {
                FluidStack s = handler.getFluidInTank(o);
                if (!s.isEmpty()) {
                    myInputFluids.add(s.copy());
                }
            }
        }

        // Iterate over each required input
        for (String input : inputs.keySet()) {
            int required = inputs.get(input);

            // Try to satisfy the fluid requirement first
            for (int i = 0; i < myInputFluids.size(); i++) {
                FluidStack s = myInputFluids.get(i);
                if (matches(input, s)) {
                    int count = s.getAmount();
                    int toFill = Math.min(required, count);
                    required -= toFill;
                    s.shrink(toFill);
                    if (required == 0) break;
                }
            }

            // If fluids are not enough, try to satisfy the item requirement
            if (required > 0) {
                for (int i = 0; i < myInputItems.size(); i++) {
                    ItemStack s = myInputItems.get(i);
                    if (matches(input, s)) {
                        int count = s.getCount();
                        int toFill = Math.min(required, count);
                        required -= toFill;
                        s.shrink(toFill);
                        if (required == 0) break;
                    }
                }
            }

            // If the required amount is still not satisfied, return false
            if (required > 0) {
                return false;
            }
        }

        // All required inputs were satisfied
        return true;
    }

}
