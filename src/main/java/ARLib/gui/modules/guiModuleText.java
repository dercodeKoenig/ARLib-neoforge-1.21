package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class guiModuleText extends GuiModuleBase {

    public String text;
    public int color;
    public boolean makeShadow;

    public guiModuleText(String text, IGuiHandler guiHandler, int x, int y, int color, boolean makeShadow) {
        super(-1, guiHandler, x, y);
        this.text = text;
        this.color = color;
        this.makeShadow = makeShadow;
    }

    public void setText(String text){
        this.text = text;
        if(FMLEnvironment.dist != Dist.CLIENT){

        }
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        if(isEnabled) {
            guiGraphics.drawString(Minecraft.getInstance().font, text, onGuiX, onGuiY, color, makeShadow);
        }
    }
}
