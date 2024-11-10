package ARLib.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class GuiInventoryHandler {
    public static void handleInventoryClick(Player player, int slot, int button, boolean isShift) {

        // Left-click
        if (button == 0) {
            if (isShift) {
                //shiftClick(player, stack);
            } else {
                leftClick(slot, player);
            }
        }

        // Right-click
        else if (button == 1) {
            //rightClick(stack, carriedStack);
        }
    }
    // Method to handle left-click without shift
    private static void leftClick(int slot, Player player) {

    }


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

    }
/*
    // Method to handle right-click without shift
    private static void rightClick(ItemStack stack, ItemStack carriedStack) {

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

