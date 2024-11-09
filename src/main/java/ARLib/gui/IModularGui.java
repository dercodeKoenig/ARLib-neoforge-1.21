package ARLib.gui;

import java.util.List;

public interface IModularGui {
    List<guiModuleBase> getModules();
    void onGuiTick();
}
