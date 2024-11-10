package ARLib.gui;

import ARLib.network.PacketBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

public class GuiHandlerBlockEntity {

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


    public static void serverTick(GuiHandlerBlockEntity guiHandler) {
            if (!guiHandler.playersTrackingGui.isEmpty()) {

                for (guiModuleBase m : guiHandler.modules) {
                    m.serverTick();
                }

                // if a player has not sent a gui ping for 2 seconds, he no longer has the gui open
                for (UUID uid : guiHandler.playersTrackingGui.keySet()) {
                    guiHandler.playersTrackingGui.put(uid, guiHandler.playersTrackingGui.get(uid) + 1);
                    if (guiHandler.playersTrackingGui.get(uid) > 40) {
                        guiHandler.playersTrackingGui.remove(uid);
                    }
                }
            }
    }

    public void openGui() {
        openGui(176, 166);
    }
    public void openGui(int w, int h) {
        last_ping = 99999; // ping asap
        Minecraft.getInstance().setScreen(new modularBlockEntityScreen(this,modules,w,h));
    }

    public void onGuiClose() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("closeGui",Minecraft.getInstance().player.getUUID());
        PacketDistributor.sendToServer(PacketBlockEntity.getBlockEntityPacket(parentBE, tag));
    }


    public void onGuiClientTick() {
        last_ping += 1;
        if (last_ping > 20) {
            last_ping = 0;
            CompoundTag tag = new CompoundTag();
            tag.putUUID("guiPing", Minecraft.getInstance().player.getUUID());
            PacketDistributor.sendToServer(PacketBlockEntity.getBlockEntityPacket(parentBE, tag));
        }

    }

    public void sendToTrackingClients(CompoundTag tag) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (UUID uid : playersTrackingGui.keySet()) {
            PacketDistributor.sendToPlayer(server.getPlayerList().getPlayer(uid), PacketBlockEntity.getBlockEntityPacket(parentBE, tag));
        }
    }

    public void readServer(CompoundTag tag) {
        if (tag.contains("guiPing")) {
            UUID uid = tag.getUUID("guiPing");
            // update data asap when a client opens the gui new
            if (!playersTrackingGui.containsKey(uid)){
                CompoundTag guiData = new CompoundTag();
                for (guiModuleBase guiModule:modules){
                    guiModule.writeDataToTag(guiData);
                }
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                PacketDistributor.sendToPlayer(server.getPlayerList().getPlayer(uid),PacketBlockEntity.getBlockEntityPacket(parentBE,guiData));
            }
            playersTrackingGui.put(uid, 0);
        }
        if (tag.contains("closeGui")) {
            UUID uid = tag.getUUID("closeGui");
            // a client said he no longer has the gui open
            if (playersTrackingGui.containsKey(uid)){
                playersTrackingGui.remove(uid);
            }
        }
        for (guiModuleBase m : modules) {
            m.readServer(tag);
        }
    }

    public void readClient(CompoundTag tag) {
        for (guiModuleBase m : modules) {
            m.readClient(tag);
        }
    }
}
