package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class guiModuleText extends GuiModuleBase {

    String text;
    int color;
    boolean makeShadow;

    public guiModuleText(String text, IGuiHandler guiHandler, int x, int y, int color, boolean makeShadow) {
        super(-1, guiHandler, x, y);
        this.text = text;
        this.color = color;
        this.makeShadow = makeShadow;
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.drawString(Minecraft.getInstance().font, text, onGuiX, onGuiY, color, makeShadow);
    }
}
