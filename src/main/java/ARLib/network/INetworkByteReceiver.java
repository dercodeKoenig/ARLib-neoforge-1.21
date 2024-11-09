package ARLib.network;

import net.minecraft.nbt.CompoundTag;

// INetworkPacket.java
public interface INetworkByteReceiver {
    void readServer(CompoundTag tag);
    void readClient(CompoundTag tag);
}
