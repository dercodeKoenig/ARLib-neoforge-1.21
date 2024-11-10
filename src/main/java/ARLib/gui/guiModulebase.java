package ARLib.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

public class guiModuleBase {

    int x;
    int y;
    int id;

    int onGuiX;
    int onGuiY;

    GuiHandlerBlockEntity guiHandler;
    public guiModuleBase(int id, GuiHandlerBlockEntity guiHandler, int x, int y) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.guiHandler = guiHandler;
        this.onGuiX = 0;
        this.onGuiY = 0;
    }

    public void setGuiOffset(int left, int top){
        onGuiX = x+left;
        onGuiY = y+top;
    }

    public boolean isMouseOver(double mouseX, double mouseY, int x,int y,int w,int h) {
        return mouseX >= x &&
                mouseX <= x + w &&
                mouseY >= y &&
                mouseY <= y + h;
    }

    public void onMouseCLick(double x, double y, int button) {

    }

    public void readServer(CompoundTag tag) {

    }

    public void readClient(CompoundTag tag) {

    }

    public void serverTick() {

    }

    public void writeDataToTag(CompoundTag tag){

    }

    public  void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

    }

    String getMyTagKey(){
        return "moduleTag"+this.id;
    }
}
