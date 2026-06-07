package morgan.lesbos.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lesbos.Lesbos;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record MovingSoundS2CPacket(Identifier soundId, float volume, float pitch, int entityId, long seed) implements CustomPayload {
    public static final Id<MovingSoundS2CPacket> ID = new Id<>(Lesbos.id("moving_sound"));

    public static final PacketCodec<RegistryByteBuf, MovingSoundS2CPacket> CODEC =
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC, MovingSoundS2CPacket::soundId,
                    PacketCodecs.FLOAT, MovingSoundS2CPacket::volume,
                    PacketCodecs.FLOAT, MovingSoundS2CPacket::pitch,
                    PacketCodecs.VAR_INT, MovingSoundS2CPacket::entityId,
                    PacketCodecs.VAR_LONG, MovingSoundS2CPacket::seed,
                    MovingSoundS2CPacket::new
            );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}