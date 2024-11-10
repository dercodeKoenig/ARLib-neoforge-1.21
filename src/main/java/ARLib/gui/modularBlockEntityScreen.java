package ARLib.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class modularBlockEntityScreen extends Screen {

    ResourceLocation background = ResourceLocation.fromNamespaceAndPath("arlib", "textures/gui/simple_gui_background.png");

    int guiW = 176;
    int guiH = 166;

    List<guiModuleBase> modules;
    GuiHandlerBlockEntity c;
    public modularBlockEntityScreen(GuiHandlerBlockEntity c, List<guiModuleBase> modules, int w, int h) {
        super(Component.literal("Screen"));
        this.c = c;
        this.modules = modules;

    }

    @Override
    protected void init() {
        super.init();
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

        for (guiModuleBase m : modules){
            m.onMouseCLick(x,y,button);
        }
        System.out.println(InputConstants.Type.MOUSE.getOrCreate(button));
        boolean isShiftDown =
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT) ||
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),InputConstants.KEY_RSHIFT);
        System.out.println(isShiftDown);
        return super.mouseClicked(x, y, button);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int left = (this.width - guiW) / 2;
        int top = (this.height - guiH) / 2;

        guiGraphics.fill(0, 0, this.width, this.height, 0x30000000); // Semi-transparent black
        guiGraphics.blit(
                background,
                left, top, 0, 0, 0, guiW, guiH, 176, 171
        );
        for (guiModuleBase m : modules) {
            m.render(guiGraphics, mouseX, mouseY, partialTick, left, top);
        }

        guiGraphics.renderItem(Minecraft.getInstance().player.inventoryMenu.getCarried(),mouseX,mouseY);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

}