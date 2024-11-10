package ARLib.gui;

import ARLib.utils.ItemStackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.checkerframework.checker.units.qual.N;

public class guiModuleItemSlot extends guiModuleBase{

    ResourceLocation slot_background = ResourceLocation.fromNamespaceAndPath("arlib","textures/gui/gui_item_slot_background.png");
    int slot_bg_w = 18;
    int slot_bg_h = 18;

    int w = 18;
    int h = 18;
    IItemHandler itemHandler;
    int targetSlot;


    ItemStack stack;
    ItemStack lastStack;

    @Override
    public void writeDataToTag(CompoundTag tag){
        CompoundTag myTag = new CompoundTag();
        RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();
        if (!itemHandler.getStackInSlot(targetSlot).isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            myTag.put("ItemStack", itemHandler.getStackInSlot(targetSlot).save(registryAccess, itemTag));
        }
        tag.put(getMyTagKey(),myTag);
    }
    @Override
    public void serverTick(){
        stack = itemHandler.getStackInSlot(targetSlot);
        if (!ItemStack.isSameItemSameComponents(stack,lastStack) || stack.getCount() != lastStack.getCount()){
            CompoundTag tag = new CompoundTag();
            writeDataToTag(tag);
            this.guiHandler. sendToTrackingClients(tag);
        }
        lastStack = stack.copy();
    }
    @Override
    public void readClient(CompoundTag tag) {
        if (tag.contains(getMyTagKey())) {
            CompoundTag myTag = tag.getCompound(getMyTagKey());
            RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
            if (myTag.contains("ItemStack")) {
                this.stack = ItemStack.parse(registryAccess, myTag.getCompound("ItemStack")).orElse(ItemStack.EMPTY);
            } else {
                this.stack = ItemStack.EMPTY;
            }
        }
    }

    public guiModuleItemSlot(int id, IItemHandler itemHandler, int targetSlot, GuiHandlerBlockEntity guiHandler, int x, int y) {
        super(id,guiHandler,x, y);
        this.targetSlot = targetSlot;
        this.itemHandler = itemHandler;
        stack =ItemStack.EMPTY;
        lastStack =ItemStack.EMPTY;
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

        if(!stack.isEmpty()){
            guiGraphics.renderItem(stack,x+left+2,y+top+2);
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack,x+left+2,y+top+2);
        }
    }
}
