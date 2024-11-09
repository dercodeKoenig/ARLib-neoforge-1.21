package ARLib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.awt.*;

public class guiModuleEnergy extends guiModulebase{

    IEnergyStorage energyStorage;
    int w;
    int h;

    public guiModuleEnergy(IEnergyStorage energyStorage, int x, int y, int w, int h){
        super(x,y);
        this.w = w;
        this.h = h;
        this.energyStorage = energyStorage;
    }

    @Override
    public  void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick,
            int left,
            int top
    ) {
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                String.valueOf(energyStorage.getEnergyStored())
                ,x+left,y+top,
                0xFF000000
        );
        System.out.println("moduleEnergyRender");
    }
}
