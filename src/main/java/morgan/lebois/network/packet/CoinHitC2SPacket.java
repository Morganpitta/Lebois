package morgan.lebois.network.packet;

import morgan.lebois.Lebois;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CoinHitC2SPacket(int entityId) implements CustomPayload {
    public static final CustomPayload.Id<CoinHitC2SPacket> ID = new CustomPayload.Id<>(Lebois.id("coin_hit"));
    public static final PacketCodec<RegistryByteBuf, CoinHitC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, CoinHitC2SPacket::entityId,
            CoinHitC2SPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
