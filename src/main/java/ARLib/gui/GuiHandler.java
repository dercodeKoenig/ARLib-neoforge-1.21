package ARLib.gui;

import ARLib.network.PacketBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GuiHandler {

    CustomPacketPayload getNetworkPacketForTag(CompoundTag tag);

    void onGuiClientTick();


    void registerModule(guiModuleBase guiModule);

    List<guiModuleBase> getModules();

    Map<UUID, Integer> getPlayersTrackingGui();

     default void openGui() {
        openGui(176, 166);
    }
     default void openGui(int w, int h) {
        sendPing();
        Minecraft.getInstance().setScreen(new modularScreen(this,w,h));
    }


    default void onGuiClose() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("closeGui",Minecraft.getInstance().player.getUUID());
        sendToServer(tag);
    }

    default void sendToServer(CompoundTag tag){
        PacketDistributor.sendToServer(getNetworkPacketForTag(tag));
    }

        default void readClient(CompoundTag tag) {
        for (guiModuleBase m : getModules()) {
            m.client_handleDataSyncedToClient(tag);
        }
    }
    default void sendToTrackingClients(CompoundTag tag) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (UUID uid : getPlayersTrackingGui().keySet()) {
            PacketDistributor.sendToPlayer(server.getPlayerList().getPlayer(uid),getNetworkPacketForTag(tag));
        }
    }

    static void serverTick(GuiHandler guiHandler) {
        if (!guiHandler.getPlayersTrackingGui().isEmpty()) {
            for (guiModuleBase m : guiHandler.getModules()) {
                m.serverTick();
            }
            // if a player has not sent a gui ping for 10 seconds, he no longer has the gui open
            // this should usually not happen because the client will unregister itself on gui close but just to be safe....
            for (UUID uid : guiHandler.getPlayersTrackingGui().keySet()) {
                guiHandler.getPlayersTrackingGui().put(uid, guiHandler.getPlayersTrackingGui().get(uid) + 1);
                if (guiHandler.getPlayersTrackingGui().get(uid) > 200) {
                    guiHandler.removePlayerFromGui(uid);
                }
            }
        }
    }

    default void removePlayerFromGui(UUID uid){
        getPlayersTrackingGui().remove(uid);
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


    default void readServer(CompoundTag tag) {
        if (tag.contains("guiPing")) {
            UUID uid = tag.getUUID("guiPing");
            // update data asap when a client opens the gui new
            if (!getPlayersTrackingGui().containsKey(uid)){
                CompoundTag guiData = new CompoundTag();
                for (guiModuleBase guiModule:getModules()){
                    guiModule.server_writeDataToSyncToClient(guiData);
                }
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                PacketDistributor.sendToPlayer(server.getPlayerList().getPlayer(uid),getNetworkPacketForTag(tag));
            }
            getPlayersTrackingGui().put(uid, 0);
        }
        if (tag.contains("closeGui")) {
            UUID uid = tag.getUUID("closeGui");
            // a client said he no longer has the gui open
            if (getPlayersTrackingGui().containsKey(uid)){
                removePlayerFromGui(uid);
            }
        }
        for (guiModuleBase m : getModules()) {
            m.server_readNetworkData(tag);
        }
    }

    default void sendPing(){
        CompoundTag tag = new CompoundTag();
        tag.putUUID("guiPing", Minecraft.getInstance().player.getUUID());
        sendToServer(tag);
    }

}