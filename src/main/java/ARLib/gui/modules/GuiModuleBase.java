package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

public class GuiModuleBase {

    protected int x;
    protected int y;
    protected int id;

    protected int onGuiX;
    protected int onGuiY;

    protected IGuiHandler guiHandler;
    public GuiModuleBase(int id, IGuiHandler guiHandler, int x, int y) {
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

    public void client_onMouseScrolled(double mouseX,double  mouseY, double scrollX,double scrollY){

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

    protected String getMyTagKey(){
        return "moduleTag"+this.id;
    }
}
