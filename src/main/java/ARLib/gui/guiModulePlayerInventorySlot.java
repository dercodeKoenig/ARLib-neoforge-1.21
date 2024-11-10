package ARLib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class guiModulePlayerInventorySlot extends guiModuleBase{

    ResourceLocation slot_background = ResourceLocation.fromNamespaceAndPath("arlib","textures/gui/gui_item_slot_background.png");
    int slot_bg_w = 18;
    int slot_bg_h = 18;

    int w = 18;
    int h = 18;
    int targetSlot;


    @Override
    public void writeDataToTag(CompoundTag tag){

    }
    @Override
    public void serverTick(){

    }
    @Override
    public void readClient(CompoundTag tag) {

    }

    public guiModulePlayerInventorySlot(int id, int targetSlot, GuiHandlerBlockEntity guiHandler, int x, int y) {
        super(id,guiHandler,x, y);
        this.targetSlot = targetSlot;
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

        ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(targetSlot);

        if(!stack.isEmpty()){
            guiGraphics.renderItem(stack,x+left+2,y+top+2);
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack,x+left+2,y+top+2);
        }
    }

    public static List<guiModulePlayerInventorySlot> makePlayerHotbarModules(int x, int y, int startingId, GuiHandlerBlockEntity guiHandler){
        List<guiModulePlayerInventorySlot> modules = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            guiModulePlayerInventorySlot s = new guiModulePlayerInventorySlot(startingId+i,0+i,guiHandler,x+i*18,y);
            modules.add(s);
        }

        return modules;
    }

    public static List<guiModulePlayerInventorySlot> makePlayerInventoryModules(int x, int y, int startingId, GuiHandlerBlockEntity guiHandler){
        List<guiModulePlayerInventorySlot> modules = new ArrayList<>();

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 9; i++) {
                guiModulePlayerInventorySlot s = new guiModulePlayerInventorySlot(
                        startingId+i+9*j,
                        9+i+9*j,
                        guiHandler,
                        x+i*18,
                        y+j*18
                );
                modules.add(s);
            }
        }


        return modules;
    }
}
