package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import ARLib.gui.modules.GuiModuleBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class guiModuleProgressBarHorizontal6px extends GuiModuleBase {
    int color;
    double progress;

    public guiModuleProgressBarHorizontal6px(int id, int barColor, IGuiHandler guiHandler, int x, int y) {
        super(id, guiHandler, x, y);
        this.color = barColor;
    }

    public ResourceLocation background = ResourceLocation.fromNamespaceAndPath("arlib", "textures/gui/gui_horizontal_progress_bar_background.png");

    @OnlyIn(Dist.CLIENT)
    public void setProgress(double progress) {
        this.progress = progress;
    }


    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.blit(background, onGuiX, onGuiY, 0, 0, 54, 6, 54, 6);
        guiGraphics.fill(onGuiX + 1, onGuiY + 1, onGuiX+(int) (52 * progress)+1, onGuiY+4+1, color);

    }
}
