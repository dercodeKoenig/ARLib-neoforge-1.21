package ARLib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class modularScreen extends Screen {

    ResourceLocation background = ResourceLocation.fromNamespaceAndPath("arlib", "textures/gui/simple_gui_background.png");


    int guiW = 176;
    int guiH = 166;

    List<guiModuleBase> modules;
    IModularGui c;

    public modularScreen(IModularGui c, int w, int h) {
        super(Component.literal("Screen"));
        this.c = c;
        modules = c.getModules();
    }

    public modularScreen(IModularGui c) {
        this(c, 176, 166);
    }


    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void tick() {
        c.onGuiTick();
        for (guiModuleBase m : modules) {
            m.tick();
        }
        super.tick();

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
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}