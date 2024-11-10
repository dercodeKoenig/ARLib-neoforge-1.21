package ARLib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

public class guiModuleBase {

    int x;
    int y;
    int id;
    GuiHandlerBlockEntity guiHandler;
    public guiModuleBase(int id, GuiHandlerBlockEntity guiHandler, int x, int y){
        this.x = x;
        this.y = y;
        this.id=id;
        this.guiHandler = guiHandler;
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
            float partialTick,
            int left,
            int top
    ) {

    }

    String getMyTagKey(){
        return "moduleTag"+this.id;
    }
}
