package ARLib.gui;

import ARLib.network.PacketBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;


/**
 * How to use a GuiHandler on a BlockEntity
 *
 *
 *  - create a GuiHandlerBlockEntity instance in you BlockEntity class:
 *                 GuiHandler guiHandler = new GuiHandlerBlockEntity(this);
 *
 *
 *  - register gui modules:
 *                  [your_gui_handler_instance].registerModule(
 *                      new guiModuleEnergy(0,[your_IEnergyStorage_object],guiHandler,10,10)
 *                  );
 *
 *  - implement INetworkTagReceiver in your BlockEntity class
 *    This GuiHandlr uses PacketBlockEntity to send data to your BlockEntity.
 *    You need to forward this data to the GuiHandler:
 *
 *          in  readServer(CompoundTag tag) call [your_gui_handler_instance].readServer(CompoundTag tag)
 *          in  readClient(CompoundTag tag) call [your_gui_handler_instance].readClient(CompoundTag tag)
 *
 *  - register your BlockEntity to have a tick() method
 *    In GuiHandler.serverTick(...), the server scans for changes in the gui data if one or more clients watch the gui.
 *    You need to call guiHandler.serverTick([your_gui_handler_instance]) on server side every tick to allow data sync.
 *    If no clients watch the gui, serverTick will instantly return to keep the code efficient and not waste time.
 *
 *  - open your gui from anywhere using [your_gui_handler_instance].openGui(), for example on block click.
 */
public class GuiHandlerBlockEntity implements GuiHandler {

    Map<UUID, Integer> playersTrackingGui;
    List<guiModuleBase> modules;
    int last_ping = 0;
    BlockEntity parentBE;

    public GuiHandlerBlockEntity(BlockEntity parentBlockEntity) {
        this.playersTrackingGui = new HashMap<>();
        modules = new ArrayList<>();
        this.parentBE = parentBlockEntity;
    }

    public void registerModule(guiModuleBase guiModule) {
        modules.add(guiModule);
    }

    @Override
    public List<guiModuleBase> getModules() {
        return modules;
    }
    @Override
    public Map<UUID, Integer> getPlayersTrackingGui(){
        return playersTrackingGui;
    }

    @Override
    public CustomPacketPayload getNetworkPacketForTag(CompoundTag tag) {
        return PacketBlockEntity.getBlockEntityPacket(parentBE,tag);
    }

    public void onGuiClientTick() {
        last_ping += 1;
        if (last_ping > 20) {
            last_ping = 0;
            sendPing();
        }
    }

}
