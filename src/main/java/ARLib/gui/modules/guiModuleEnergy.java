package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class guiModuleEnergy extends GuiModuleBase {

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


    int maxEnergy;
    int energy;
    int last_energy;

    @Override
    public void server_writeDataToSyncToClient(CompoundTag tag){
        CompoundTag myTag = new CompoundTag();
        myTag.putInt("moduleId",this.id);
        myTag.putInt("energy",energyStorage.getEnergyStored());
        tag.put(getMyTagKey(),myTag);
    }

    int last_update = 0;
    @Override
    public void serverTick(){
        last_update+=1;
        energy = energyStorage.getEnergyStored();
        // update every 5 ticks
        if (energy != last_energy && last_update > 5){
            last_update = 0;
            last_energy = energy;
            CompoundTag tag = new CompoundTag();
            server_writeDataToSyncToClient(tag);
            this.guiHandler. sendToTrackingClients(tag);
        }
    }
    @Override
    public void client_handleDataSyncedToClient(CompoundTag tag){
        if(tag.contains(getMyTagKey())){
            CompoundTag myTag = tag.getCompound(getMyTagKey());
            this.energy = myTag.getInt("energy");
        }
    }

    public guiModuleEnergy(int id, IEnergyStorage energyStorage, IGuiHandler guiHandler, int x, int y){
        super(id,guiHandler,x,y);
        this.energyStorage = energyStorage;
        serverTick();
        maxEnergy = energyStorage.getMaxEnergyStored();
    }

    @Override
    public  void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        double relative_energy_level = (double)  energy / maxEnergy;
        int v_offset = (int) ((1-relative_energy_level)*bar_size_h);
        int v_offset_tex = (int) ((1-relative_energy_level)*energy_bar_th);

        guiGraphics.blit(energy_bar_background,onGuiX,onGuiY,0,0,w, h,energy_bar_background_tw,energy_bar_background_th);

        //guiGraphics.blit(energy_bar,x+left+energy_bar_offset_x,y+top+energy_bar_offset_y,0,v_offset,energy_bar_tw, energy_bar_th-v_offset);

        guiGraphics.blit(
                energy_bar,
                onGuiX+energy_bar_offset_x,onGuiY+v_offset+energy_bar_offset_y,
                bar_size_w,bar_size_h-v_offset,
                (float)0,(float)0+v_offset_tex,
                energy_bar_tw,energy_bar_th-v_offset_tex,
                energy_bar_tw,energy_bar_th
                );
        if(client_isMouseOver(mouseX,mouseY,onGuiX,onGuiY,w,h)){
            String info = energy+"/"+maxEnergy+"RF";
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(info),mouseX,mouseY);
        }

    }
}
