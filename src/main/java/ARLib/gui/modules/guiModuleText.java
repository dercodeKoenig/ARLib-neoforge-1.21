package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class guiModuleText extends GuiModuleBase {

    String text;

    public guiModuleText(String text, IGuiHandler guiHandler, int x, int y) {
        super(-1, guiHandler, x, y);
        this.text = text;
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, text, onGuiX, onGuiY, 0xFFFFFFFF);
    }
}
