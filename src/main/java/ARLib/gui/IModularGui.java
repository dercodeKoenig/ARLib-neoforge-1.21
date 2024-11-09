package ARLib.gui;

import java.util.List;

public interface IModularGui {
    List<guiModulebase> getModules();
    void onGuiTick();
}
