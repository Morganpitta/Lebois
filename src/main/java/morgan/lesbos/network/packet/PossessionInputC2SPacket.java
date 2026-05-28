package morgan.lesbos.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.PossessionInterface;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public record PossessionInputC2SPacket(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking) implements CustomPayload {
    public static final Id<PossessionInputC2SPacket> ID = new Id<>(Lesbos.id("possession_input"));
    public static final PacketCodec<ByteBuf, PossessionInputC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, PossessionInputC2SPacket::sidewaysSpeed,
            PacketCodecs.FLOAT, PossessionInputC2SPacket::forwardSpeed,
            PacketCodecs.BOOL, PossessionInputC2SPacket::jumping,
            PacketCodecs.BOOL, PossessionInputC2SPacket::sneaking,
            PossessionInputC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }

    public static void handle(PossessionInputC2SPacket payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.player().getServer();

        if ( server == null ) return;

        server.execute(() -> {
            ServerPlayerEntity player = context.player();
            MobEntity entity = ((PossessionInterface) (Object) player).lesbos$getPossessedEntity();
            if (entity == null) return;

            entity.sidewaysSpeed = payload.sidewaysSpeed();
            entity.forwardSpeed = payload.forwardSpeed();
            entity.setJumping(payload.jumping());
            entity.setSneaking(payload.sneaking());
        });
    }
}