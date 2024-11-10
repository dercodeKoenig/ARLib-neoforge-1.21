package ARLib.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class GuiInventoryHandler {

    public static void handleInventoryClick(Player player, IItemHandler itemHandler, int slot, int button, boolean isShift) {

        // Left-click
        if (button == 0) {
            if (isShift) {
                //shiftClick(player, stack);
            } else {
                leftClick(itemHandler,slot, player);
            }
        }

        // Right-click
        else if (button == 1) {
            //rightClick(stack, carriedStack);
        }
    }

    // Method to handle left-click without shift
    private static void leftClick(IItemHandler handler,int slot, Player player) {
        ItemStack carriedStack = player.inventoryMenu.getCarried();
        ItemStack stack = handler.getStackInSlot(slot);

        if (carriedStack.isEmpty() && !stack.isEmpty()) {
            // Pick up the stack
            player.inventoryMenu.setCarried(handler.extractItem(slot,stack.getCount(),false));

        } else if (stack.isEmpty() && !carriedStack.isEmpty()) {
            // Place down the carried item
            player.inventoryMenu.setCarried(handler.insertItem(slot,carriedStack,false));

        } else if (!stack.isEmpty() && !carriedStack.isEmpty() && ItemStack.isSameItemSameComponents(stack,carriedStack)) {
            // Add to stack
            int transferAmount = Math.min(handler.getSlotLimit(slot) - stack.getCount(), carriedStack.getCount());
            player.inventoryMenu.setCarried(handler.insertItem(slot,carriedStack.copyWithCount(transferAmount),false));
        }
    }
/*
    // Method to handle right-click without shift
    private static void rightClick(ItemStack stack, ItemStack carriedStack) {
        if (carriedStack.isEmpty() && !stack.isEmpty()) {
            // Pick up half of the stack
            int halfCount = stack.getCount() / 2;
            carriedStack.setItem(stack.getItem());
            carriedStack.setCount(halfCount);
            stack.shrink(halfCount);
        } else if (stack.isEmpty() && !carriedStack.isEmpty()) {
            // Place one item from carried stack
            stack.setItem(carriedStack.getItem());
            stack.setCount(1);
            carriedStack.shrink(1);
        } else if (!stack.isEmpty() && !carriedStack.isEmpty() && stack.sameItem(carriedStack)) {
            // Add one item to stack
            if (stack.getCount() < stack.getMaxStackSize()) {
                stack.grow(1);
                carriedStack.shrink(1);
            }
        }
    }
 */

    /*
    // Method to handle shift-click (move item between stacks)
    private static void shiftClick(Player player, ItemStack stack) {
        if (!stack.isEmpty()) {
            // Attempt to move the stack to the player's inventory
            boolean moved = player.inventoryMenu.add(stack);
            if (moved) {
                stack.setCount(0); // Clear the stack if moved successfully
            }
        }
    }
     */
}

