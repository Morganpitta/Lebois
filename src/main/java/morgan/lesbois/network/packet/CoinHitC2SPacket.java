package morgan.lesbois.network.packet;

import morgan.lesbois.Lesbois;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record CoinHitC2SPacket(int entityId) implements CustomPayload {
    public static final CustomPayload.Id<CoinHitC2SPacket> ID = new CustomPayload.Id<>(Lesbois.id("coin_hit"));
    public static final PacketCodec<RegistryByteBuf, CoinHitC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, CoinHitC2SPacket::entityId,
            CoinHitC2SPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
