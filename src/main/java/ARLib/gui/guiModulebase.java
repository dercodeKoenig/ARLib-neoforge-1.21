package ARLib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

public class guiModuleBase {

    int x;
    int y;
    int id;
    GuiCapableBlockEntity guiTile;
    public guiModuleBase(int id, GuiCapableBlockEntity guiTile, int x, int y){
        this.x = x;
        this.y = y;
        this.id=id;
        this.guiTile = guiTile;
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
}
