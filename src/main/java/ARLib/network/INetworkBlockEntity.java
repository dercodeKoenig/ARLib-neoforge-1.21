package ARLib.network;
// INetworkPacket.java
public interface INetworkBlockEntity {
    void read_bytes(int packetid, byte[] bytes);
    byte[] write_bytes(int packetid);
}
