package ARLib.gui;

import ARLib.gui.modules.GuiModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.print.attribute.standard.Sides;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IGuiHandler {

    CustomPacketPayload getNetworkPacketForTag(CompoundTag tag);

    void onGuiClientTick();

    void registerModule(GuiModuleBase guiModule);

    List<GuiModuleBase> getModules();

    Map<UUID, Integer> getPlayersTrackingGui();

    @OnlyIn(Dist.CLIENT)
     default void openGui() {
        openGui(176, 166);
    }
        @OnlyIn(Dist.CLIENT)
     default void openGui(int w, int h) {
        sendPing();
        Minecraft.getInstance().setScreen(new ModularScreen(this,w,h));
    }

    @OnlyIn(Dist.CLIENT)
    default void onGuiClose() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("closeGui",Minecraft.getInstance().player.getUUID());
        sendToServer(tag);
    }
    @OnlyIn(Dist.CLIENT)
    default void sendToServer(CompoundTag tag){
        PacketDistributor.sendToServer(getNetworkPacketForTag(tag));
    }

        default void readClient(CompoundTag tag) {
        for (GuiModuleBase m : getModules()) {
            m.client_handleDataSyncedToClient(tag);
        }
    }
    default void sendToTrackingClients(CompoundTag tag) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (UUID uid : getPlayersTrackingGui().keySet()) {
            PacketDistributor.sendToPlayer(server.getPlayerList().getPlayer(uid),getNetworkPacketForTag(tag));
        }
    }

    static void serverTick(IGuiHandler guiHandler) {
        if (!guiHandler.getPlayersTrackingGui().isEmpty()) {
            for (GuiModuleBase m : guiHandler.getModules()) {
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
                for (GuiModuleBase guiModule:getModules()){
                    guiModule.server_writeDataToSyncToClient(guiData);
                }
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                PacketDistributor.sendToPlayer(server.getPlayerList().getPlayer(uid),getNetworkPacketForTag(guiData));
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
        for (GuiModuleBase m : getModules()) {
            m.server_readNetworkData(tag);
        }
    }
    @OnlyIn(Dist.CLIENT)
    default void sendPing(){
        CompoundTag tag = new CompoundTag();
        tag.putUUID("guiPing", Minecraft.getInstance().player.getUUID());
        sendToServer(tag);
    }

}