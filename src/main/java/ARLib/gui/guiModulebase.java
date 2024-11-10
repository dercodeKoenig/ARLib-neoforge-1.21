package ARLib.gui;

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

    public void client_setGuiOffset(int left, int top){
        onGuiX = x+left;
        onGuiY = y+top;
    }

    public boolean client_isMouseOver(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x &&
                mouseX <= x + w &&
                mouseY >= y &&
                mouseY <= y + h;
    }

    public void client_onMouseCLick(double x, double y, int button) {

    }

    public void server_readNetworkData(CompoundTag tag) {

    }

    public void client_handleDataSyncedToClient(CompoundTag tag) {

    }

    public void serverTick() {

    }

    public void server_writeDataToSyncToClient(CompoundTag tag){

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
