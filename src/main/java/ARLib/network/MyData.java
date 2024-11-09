package ARLib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MyData implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MyData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("arlib", "my_data"));

    static int byte_size = 512;

    public MyData(int id, byte[] bytes) {
        this.id = id;
        this.bytes = bytes;
    }

    int id;
    byte[] bytes;

    public int getId() {
        return id;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public static final StreamCodec<ByteBuf, MyData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MyData::getId,
            ByteBufCodecs.byteArray(byte_size),
            MyData::getBytes,
            MyData::new
    );

    public static void handleData(final MyData data, final IPayloadContext context) {
        System.out.println(data.getId());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}