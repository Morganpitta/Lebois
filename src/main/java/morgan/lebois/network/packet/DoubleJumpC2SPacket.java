package morgan.lebois.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lebois.Lebois;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record DoubleJumpC2SPacket(boolean boost, float forwardSpeed, float sidewaysSpeed) implements CustomPayload {
    public static final Id<DoubleJumpC2SPacket> ID = new Id<>(Lebois.id("double_jump"));
    public static final PacketCodec<ByteBuf, DoubleJumpC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, DoubleJumpC2SPacket::boost,
            PacketCodecs.FLOAT, DoubleJumpC2SPacket::forwardSpeed,
            PacketCodecs.FLOAT, DoubleJumpC2SPacket::sidewaysSpeed,
            DoubleJumpC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}