package ARLib.gui;

import ARLib.gui.modules.GuiModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModularScreen extends Screen {

    ResourceLocation background = ResourceLocation.fromNamespaceAndPath("arlib", "textures/gui/simple_gui_background.png");

    int guiW = 176;
    int guiH = 166;
    int leftOffset;
    int topOffset;

    IGuiHandler c;
    public ModularScreen(IGuiHandler c, int w, int h) {
        super(Component.literal("Screen"));
        this.c = c;


    }

    @Override
    protected void init() {
        super.init();
        calculateGuiOffsetAndNotifyModules();
    }
    @Override
    public void tick(){
        c.onGuiClientTick();
    }
    @Override
    public void onClose(){
        c.onGuiClose();
        super.onClose();
    }

    // In some Screen subclass
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (GuiModuleBase m : c.getModules()) {
            m.client_onMouseCLick(x, y, button);
        }
        return super.mouseClicked(x, y, button);
    }

    void calculateGuiOffsetAndNotifyModules(){
        leftOffset = (this.width - guiW) / 2;
        topOffset = (this.height - guiH) / 2;
        for (GuiModuleBase m:c.getModules()){
            m.client_setGuiOffset(leftOffset,topOffset);
        }
    }
    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft,width,height);
        calculateGuiOffsetAndNotifyModules();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {


        guiGraphics.fill(0, 0, this.width, this.height, 0x30000000); // Semi-transparent black
        guiGraphics.blit(
                background,
                leftOffset, topOffset, 0, 0, 0, guiW, guiH, 176, 171
        );
        for (GuiModuleBase m : c.getModules()) {
            m.render(guiGraphics, mouseX, mouseY, partialTick);
        }
guiGraphics.pose().translate(0,0,100);
       ModularScreen.renderItemStack (guiGraphics,mouseX-9,mouseY-9,Minecraft.getInstance().player.inventoryMenu.getCarried());
    }

    public static void renderItemStack(GuiGraphics g, int x, int y, ItemStack stack){
        if(stack.isEmpty())return;
        g.renderItem(stack,x+1,y+1);
        g.renderItemDecorations(Minecraft.getInstance().font, stack,x+1,y+1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}