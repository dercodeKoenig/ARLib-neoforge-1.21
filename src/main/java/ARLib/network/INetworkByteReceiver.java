package ARLib.network;
// INetworkPacket.java
public interface INetworkByteReceiver {
    void read_bytes(int packetid, byte[] bytes);
}
