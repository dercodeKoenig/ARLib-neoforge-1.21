package ARLib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class guiModuleItemSlot extends guiModuleBase{

    ResourceLocation slot_background = ResourceLocation.fromNamespaceAndPath("arlib","textures/gui/gui_item_slot_background.png");
    int slot_bg_w = 18;
    int slot_bg_h = 18;

    int w = 6;
    int h = 6;
    IItemHandler itemHandler;
    int targetSlot;

    public guiModuleItemSlot(int id,IItemHandler itemHandler,GuiCapableBlockEntity guiTile, int targetSlot, int x, int y) {
        super(id,guiTile,x, y);
        this.targetSlot = targetSlot;
        this.itemHandler = itemHandler;
    }

    @Override
    public  void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick,
            int left,
            int top
    ) {
        guiGraphics.blit(slot_background,x+left,y+top,0f,0f,w,h,slot_bg_w,slot_bg_h);
        ItemStack slotStack = itemHandler.getStackInSlot(targetSlot);
        if(!slotStack.isEmpty()){
            guiGraphics.renderItem(slotStack,x+left,y+top);
        }
    }
}
