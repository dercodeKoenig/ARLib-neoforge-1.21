package ARLib.gui;

import net.minecraft.client.gui.GuiGraphics;

public class guiModuleBase {

    int x;
    int y;

    public guiModuleBase(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void tick() {

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
