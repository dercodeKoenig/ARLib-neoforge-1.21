package ARLib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class guiModuleItemSlot extends guiModuleBase{

    ResourceLocation slot_background = ResourceLocation.fromNamespaceAndPath("arlib","textures/gui/gui_item_slot_background.png");
    int slot_bg_w = 18;
    int slot_bg_h = 18;

    int w = 15;
    int h = 15;
    IItemHandler itemHandler;
    int targetSlot;


    ItemStack stack;
    ItemStack lastStack;

    @Override
    public void writeDataToTag(CompoundTag tag){
        RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();
        if (!stack.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            tag.put("ItemStack", stack.save(registryAccess, itemTag));
            System.out.println(tag);
        }
        tag.putInt("moduleId",this.id);
    }
    @Override
    public void serverTick(){
        stack = itemHandler.getStackInSlot(targetSlot);
        if (!stack.equals(lastStack)){
            CompoundTag tag = new CompoundTag();
            writeDataToTag(tag);
            this.guiTile. sendToTrackingClients(tag);
        }
        lastStack = stack;
    }
    @Override
    public void readClient(CompoundTag tag){
        if(tag.contains("moduleId")){
            int moduleId=tag.getInt("moduleId");
            if(moduleId == this.id){
                RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
                if (tag.contains("ItemStack")){
                    this.stack =  ItemStack.parse(registryAccess, tag.getCompound("ItemStack")).orElse(ItemStack.EMPTY);
                }
                else{
                    this.stack = ItemStack.EMPTY;
                }
            }
        }
    }

    public guiModuleItemSlot(int id,IItemHandler itemHandler, int targetSlot, GuiCapableBlockEntity guiTile, int x, int y) {
        super(id,guiTile,x, y);
        this.targetSlot = targetSlot;
        this.itemHandler = itemHandler;
        this.stack = ItemStack.EMPTY;
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
        ItemStack slotStack = stack;
        if(!slotStack.isEmpty()){
            guiGraphics.renderItem(slotStack,x+left,y+top);
        }
    }
}
