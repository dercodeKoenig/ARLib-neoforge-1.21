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
 *                  guiHandler.registerModule(new guiModuleEnergy(0,[your IEnergyStorage],guiHandler,10,10));
 *
 *
 *  - implement INetworkTagReceiver in your BlockEntity class
 *    Gui module sync uses PacketBlockEntity to send data to your BlockEntity.
 *    You need to forward this data to the GuiHandler:
 *
 *          in  readServer(CompoundTag tag) call guiHandler.readServer(CompoundTag tag)
 *          in  readClient(CompoundTag tag) call guiHandler.readClient(CompoundTag tag)
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
