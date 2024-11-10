package ARLib.gui;

import ARLib.network.PacketBlockEntity;
import ARLib.utils.ItemStackHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.checkerframework.checker.units.qual.N;

import java.util.UUID;

public class guiModuleItemSlot extends guiModuleBase{

    ResourceLocation slot_background = ResourceLocation.fromNamespaceAndPath("arlib","textures/gui/gui_item_slot_background.png");
    int slot_bg_w = 18;
    int slot_bg_h = 18;

    int w = 18;
    int h = 18;
    IItemHandler itemHandler;
    int targetSlot;


    ItemStack stack;
    ItemStack lastStack;

    @Override
    public void onMouseCLick(double mx, double my, int button) {
        boolean isShiftDown =
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT) ||
                        InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),InputConstants.KEY_RSHIFT);

        if (isMouseOver(mx, my, onGuiX, onGuiY, w, h)) {
            CompoundTag tag = new CompoundTag();
            CompoundTag myTag = new CompoundTag();

            // add client id to the tag
            UUID myId = Minecraft.getInstance().player.getUUID();
            myTag.putUUID("uuid_from",myId);
            myTag.putInt("mouseButtonClicked",button);
            myTag.putBoolean("isShift",isShiftDown);

            tag.put(getMyTagKey(), myTag);
            guiHandler.sendToServer(tag);
        }
    }

    @Override
    public void writeDataToTag(CompoundTag tag){
        CompoundTag myTag = new CompoundTag();
        RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();
        if (!itemHandler.getStackInSlot(targetSlot).isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            myTag.put("ItemStack", itemHandler.getStackInSlot(targetSlot).save(registryAccess, itemTag));
        }
        tag.put(getMyTagKey(),myTag);
    }
    @Override
    public void serverTick(){
        stack = itemHandler.getStackInSlot(targetSlot);
        if (!ItemStack.isSameItemSameComponents(stack,lastStack) || stack.getCount() != lastStack.getCount()){
            CompoundTag tag = new CompoundTag();
            writeDataToTag(tag);
            this.guiHandler. sendToTrackingClients(tag);
        }
        lastStack = stack.copy();
    }
    @Override
    public void readClient(CompoundTag tag) {
        if (tag.contains(getMyTagKey())) {
            CompoundTag myTag = tag.getCompound(getMyTagKey());
            RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
            if (myTag.contains("ItemStack")) {
                this.stack = ItemStack.parse(registryAccess, myTag.getCompound("ItemStack")).orElse(ItemStack.EMPTY);
            } else {
                this.stack = ItemStack.EMPTY;
            }
        }
    }
    @Override
    public void readServer(CompoundTag tag) {
        if (tag.contains(getMyTagKey())) {
            CompoundTag myTag = tag.getCompound(getMyTagKey());

            if (myTag.contains("uuid_from") && myTag.contains("mouseButtonClicked") && myTag.contains("isShift")) {
                UUID from_uuid = myTag.getUUID("uuid_from");
                int button = myTag.getInt("mouseButtonClicked");
                boolean isShift = myTag.getBoolean("isShift");
                Player player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(from_uuid);

                GuiInventoryHandler.handleInventoryClick(player,itemHandler,targetSlot,button,isShift);

            }
        }
    }


    public guiModuleItemSlot(int id, IItemHandler itemHandler, int targetSlot, GuiHandlerBlockEntity guiHandler, int x, int y) {
        super(id,guiHandler,x, y);
        this.targetSlot = targetSlot;
        this.itemHandler = itemHandler;
        stack =ItemStack.EMPTY;
        lastStack =ItemStack.EMPTY;
    }

    @Override
    public  void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.blit(slot_background,onGuiX,onGuiY,0f,0f,w,h,slot_bg_w,slot_bg_h);
        if(isMouseOver(mouseX,mouseY,onGuiX,onGuiY,w,h)){
            guiGraphics.fill(onGuiX,onGuiY,w+onGuiX,h+onGuiY, 0x30FFFFFF); // Semi-transparent white
        }

            modularBlockEntityScreen.renderItemStack(guiGraphics,onGuiX,onGuiY,stack);

    }
}
