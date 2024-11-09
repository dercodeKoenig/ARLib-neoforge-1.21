package ARLib.network;

import ARLib.utils.DimensionUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class BlockEntityPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockEntityPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("arlib", "my_data"));

    static int max_size = 512;

    public BlockEntityPacket(int packetid, String dim, int x, int y, int z, byte[] bytes) {
        this.id = packetid;
        this.bytes = bytes;
        this.x=x;
        this.y=y;
        this.z=z;
        this.dim = dim;
    }

    String dim;
    int x,y,z;
    int id;
    byte[] bytes;

    public int getId() {
        return id;
    }
    public byte[] getBytes() {
        return bytes;
    }
    public String dim(){return dim;}
    public int x(){return x;}
    public int y(){return y;}
    public int z(){return z;}

    public static final StreamCodec<ByteBuf, BlockEntityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            BlockEntityPacket::getId,
            ByteBufCodecs.STRING_UTF8,
            BlockEntityPacket::dim,
            ByteBufCodecs.INT,
            BlockEntityPacket::x,
            ByteBufCodecs.INT,
            BlockEntityPacket::y,
            ByteBufCodecs.INT,
            BlockEntityPacket::z,
            ByteBufCodecs.byteArray(max_size),
            BlockEntityPacket::getBytes,
            BlockEntityPacket::new
    );

    public static void handleData(final BlockEntityPacket data, final IPayloadContext context) {
        Level world = DimensionUtils.getDimensionLevel(data.dim);
        BlockEntity tile = world.getBlockEntity(new BlockPos(data.x(),data.y(),data.z()));
        if (tile instanceof INetworkByteReceiver){
            ((INetworkByteReceiver) tile).read_bytes(data.getId(),data.getBytes());
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}