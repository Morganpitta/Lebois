package morgan.lebois.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lebois.Lebois;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record UnPossessionS2CPacket() implements CustomPayload {
    public static final Id<UnPossessionS2CPacket> ID = new Id<>(Lebois.id("un_possession"));
    public static final PacketCodec<ByteBuf, UnPossessionS2CPacket> CODEC = PacketCodec.unit(new UnPossessionS2CPacket());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}