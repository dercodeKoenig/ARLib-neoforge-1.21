package ARLib.gui;

import ARLib.network.PacketBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

                // if a player has not sent a gui ping for 10 seconds, he no longer has the gui open
                // this should usually not happen because the client will unregister itself on gui close but just to be safe....
                for (UUID uid : guiHandler.playersTrackingGui.keySet()) {
                    guiHandler.playersTrackingGui.put(uid, guiHandler.playersTrackingGui.get(uid) + 1);
                    if (guiHandler.playersTrackingGui.get(uid) > 200) {
                        guiHandler.removePlayerFromGui(uid);
                    }
                }
            }
    }

    public void openGui() {
        openGui(176, 166);
    }
    public void openGui(int w, int h) {
        sendPing();
        Minecraft.getInstance().setScreen(new modularBlockEntityScreen(this,modules,w,h));
    }

    public void onGuiClose() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("closeGui",Minecraft.getInstance().player.getUUID());
        sendToServer(tag);
    }

    public void removePlayerFromGui(UUID uid){
        playersTrackingGui.remove(uid);
        Player p = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uid);
        if (p!=null) {
            ItemStack carried = p.inventoryMenu.getCarried();
            if (!carried.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(p.level(),p.position().x,p.position().y,p.position().z,carried);
                p.inventoryMenu.setCarried(ItemStack.EMPTY);
                p.level().addFreshEntity(itemEntity);
            }
        }
    }

    void sendPing(){
        CompoundTag tag = new CompoundTag();
        tag.putUUID("guiPing", Minecraft.getInstance().player.getUUID());
        sendToServer(tag);
    }

    public void onGuiClientTick() {
        last_ping += 1;
        if (last_ping > 20) {
            last_ping = 0;
            sendPing();
        }
    }
    public void sendToServer(CompoundTag tag){
        PacketDistributor.sendToServer(PacketBlockEntity.getBlockEntityPacket(parentBE,tag));
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
                    guiModule.server_writeDataToSyncToClient(guiData);
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
                removePlayerFromGui(uid);
            }
        }
        for (guiModuleBase m : modules) {
            m.server_readNetworkData(tag);
        }
    }

    public void readClient(CompoundTag tag) {
        for (guiModuleBase m : modules) {
            m.client_handleDataSyncedToClient(tag);
        }
    }
}
