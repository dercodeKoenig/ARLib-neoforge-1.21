package ARLib.gui.modules;

import ARLib.gui.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class guiModuleFluidTankDisplay extends GuiModuleBase {

    ResourceLocation fluid_bar_background = ResourceLocation.fromNamespaceAndPath("arlib", "textures/gui/gui_vertical_progress_bar_background.png");

    IFluidHandler fluidHandler;
    int targetSlot;
    FluidStack client_myFluidStack;
    FluidStack lastFluidStack;
    int maxCapacity;


    // textures width and height
    int fluid_bar_background_tw = 14;
    int fluid_bar_background_th = 54;
    int borderpx = 1;

    // full size of the bar when rendering (background)
    int w = 14;
    int h = 54;

    // size of the bar
    // the background image is 14px * 54px with 1 py border
    // to get the size of the bar we need to scale the size
    int bar_size_w = (int) (w * (((double) fluid_bar_background_tw - borderpx * 2) / fluid_bar_background_tw));
    int bar_size_h = (int) (h * (((double) fluid_bar_background_th - borderpx * 2) / fluid_bar_background_th));

    int fluid_bar_offset_x = (int) ((double) borderpx / fluid_bar_background_tw * w);
    int fluid_bar_offset_y = (int) ((double) borderpx / fluid_bar_background_th * h);


    public guiModuleFluidTankDisplay(int id, IFluidHandler fluidHandler, int targetSlot, IGuiHandler guiHandler, int x, int y) {
        super(id, guiHandler, x, y);
        this.fluidHandler = fluidHandler;
        this.targetSlot = targetSlot;
        this.client_myFluidStack = FluidStack.EMPTY;
        this.lastFluidStack = FluidStack.EMPTY;
        this.maxCapacity = fluidHandler.getTankCapacity(targetSlot);
    }


    @Override
    public void server_writeDataToSyncToClient(CompoundTag tag) {
        CompoundTag myTag = new CompoundTag();
        RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();
        FluidStack f = fluidHandler.getFluidInTank(targetSlot);
        if (f.isEmpty()){
            myTag.putBoolean("hasFluid",false);
            System.out.println("no fluid");
        }else{
            myTag.putBoolean("hasFluid",true);
            Tag fluid = fluidHandler.getFluidInTank(targetSlot).save(registryAccess);
            myTag.put("fluid", fluid);
        }
        tag.put(getMyTagKey(), myTag);
    }

    @Override
    public void client_handleDataSyncedToClient(CompoundTag tag) {
        if (tag.contains(getMyTagKey())) {
            CompoundTag myTag = tag.getCompound(getMyTagKey());
            if(myTag.getBoolean("hasFluid")) {
                Tag fluid = myTag.get("fluid");
                RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();
                client_myFluidStack = FluidStack.parse(registryAccess, fluid).get();
            }else{
                client_myFluidStack = FluidStack.EMPTY;
            }
        }
    }

    int last_update = 0;

    @Override
    public void serverTick() {
        last_update += 1;
        // update every 5 ticks
        if (
                (!fluidHandler.getFluidInTank(targetSlot).equals(lastFluidStack) ||
                fluidHandler.getFluidInTank(targetSlot).getAmount() != lastFluidStack.getAmount())
                        && last_update > 5) {

            last_update = 0;
            lastFluidStack = fluidHandler.getFluidInTank(targetSlot).copy();
            CompoundTag tag = new CompoundTag();
            server_writeDataToSyncToClient(tag);
            this.guiHandler.sendToTrackingClients(tag);
        }
    }


    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        double relative_fluid_level = (double) client_myFluidStack.getAmount() / maxCapacity;
        int y_offset = (int) ((1 - relative_fluid_level) * bar_size_h);

        guiGraphics.blit(fluid_bar_background, onGuiX, onGuiY, 0, 0, w, h, fluid_bar_background_tw, fluid_bar_background_th);

        int color = client_myFluidStack.getFluid().defaultFluidState().createLegacyBlock().getMapColor(null, null).col;
        color = color | 0xFF000000;
        guiGraphics.fill(onGuiX + fluid_bar_offset_x, onGuiY + fluid_bar_offset_y + y_offset, onGuiX + bar_size_w, onGuiY + bar_size_h, color);

        if (client_isMouseOver(mouseX, mouseY, onGuiX, onGuiY, w, h)) {
            String info = client_myFluidStack.getFluid().toString()+":"+client_myFluidStack.getAmount()+"/"+maxCapacity+"mb";
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(info),mouseX,mouseY);
        }

    }
}
