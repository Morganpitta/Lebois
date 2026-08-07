package morgan.lebois.network.packet;

import morgan.lebois.Lebois;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncItemUseTimeS2CPacket(int useTimeLeft) implements CustomPayload {
    public static final CustomPayload.Id<SyncItemUseTimeS2CPacket> ID = new CustomPayload.Id<>(Lebois.id("sync_item_use_time"));
    public static final PacketCodec<RegistryByteBuf, SyncItemUseTimeS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SyncItemUseTimeS2CPacket::useTimeLeft,
            SyncItemUseTimeS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
