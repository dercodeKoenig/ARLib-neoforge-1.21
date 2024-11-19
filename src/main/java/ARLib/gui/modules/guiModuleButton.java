package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class guiModuleButton extends GuiModuleBase {
    int w, h;
    int textureW, textureH;
    ResourceLocation image;
String text;
@Override
public void client_onMouseCLick(double x, double y, int button) {
    if (client_isMouseOver(x, y, onGuiX, onGuiY, w, h) && button == 0) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("guiButtonClick", id);
        guiHandler.sendToServer(tag);
    }
}
    public guiModuleButton(int id,String text, IGuiHandler guiHandler, int x, int y, int w, int h, ResourceLocation image, int textureW, int textureH) {
        super(id, guiHandler, x, y);
        this.w = w;
        this.h = h;
        this.image = image;
        this.textureW = textureW;
        this.textureH = textureH;
        this.text= text;
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.blit(image, onGuiX, onGuiY, 0, 0, w, h, textureW, textureH);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, text,onGuiX+w/2,onGuiY+h/2-Minecraft.getInstance().font.lineHeight/2,0xFFFFFFFF);
    }
}
