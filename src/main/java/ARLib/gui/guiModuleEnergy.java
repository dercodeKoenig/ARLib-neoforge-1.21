package ARLib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class guiModuleEnergy extends guiModuleBase {

    ResourceLocation energy_bar_background = ResourceLocation.fromNamespaceAndPath("arlib","textures/gui/gui_vertical_progress_bar_background.png");
    ResourceLocation energy_bar = ResourceLocation.fromNamespaceAndPath("arlib","textures/gui/gui_vertical_progress_bar.png");


    IEnergyStorage energyStorage;

    // textures width and height
    int energy_bar_background_tw = 14;
    int energy_bar_background_th = 54;
    int borderpx = 1;
    int energy_bar_tw = 6;
    int energy_bar_th = 44;

    // full size of the bar when rendering (background)
    int w = 14;
    int h = 54;

    // size of the bar
    // the background image is 14px * 54px with 1 py border
    // to get the size of the bar we need to scale the size
    int bar_size_w = (int) (w * (((double)energy_bar_background_tw-borderpx*2) / energy_bar_background_tw));
    int bar_size_h = (int) (h * (((double)energy_bar_background_th-borderpx*2) / energy_bar_background_th));

    int energy_bar_offset_x = (int) ((double)borderpx / energy_bar_background_tw * w);
    int energy_bar_offset_y = (int) ((double)borderpx / energy_bar_background_th * h);



    public guiModuleEnergy(IEnergyStorage energyStorage, int x, int y){
        super(x,y);
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

        double relative_energy_level = (double) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
        int v_offset = (int) ((1-relative_energy_level)*bar_size_h);

        guiGraphics.blit(energy_bar_background,x+left,y+top,0,0,energy_bar_background_tw, energy_bar_background_th);

        //guiGraphics.blit(energy_bar,x+left+energy_bar_offset_x,y+top+energy_bar_offset_y,0,v_offset,energy_bar_tw, energy_bar_th-v_offset);

        guiGraphics.blit(
                energy_bar,
                x+left+energy_bar_offset_x,y+top+v_offset+energy_bar_offset_y,
                bar_size_w,bar_size_h-v_offset,
                (float)0,(float)0+v_offset,
                energy_bar_tw,energy_bar_th-v_offset,
                energy_bar_tw,energy_bar_th
                );
    }
}
