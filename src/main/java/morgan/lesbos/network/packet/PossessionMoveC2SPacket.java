package morgan.lesbos.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.PossessionInterface;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public record PossessionMoveC2SPacket(double x, double y, double z, float yaw, float pitch) implements CustomPayload {
    public static final Id<PossessionMoveC2SPacket> ID = new Id<>(Lesbos.id("possession_input"));
    public static final PacketCodec<ByteBuf, PossessionMoveC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, PossessionMoveC2SPacket::x,
            PacketCodecs.DOUBLE, PossessionMoveC2SPacket::y,
            PacketCodecs.DOUBLE, PossessionMoveC2SPacket::z,
            PacketCodecs.FLOAT, PossessionMoveC2SPacket::yaw,
            PacketCodecs.FLOAT, PossessionMoveC2SPacket::pitch,
            PossessionMoveC2SPacket::new
    );

    public PossessionMoveC2SPacket(PlayerEntity entity) {
        this(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
    }

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }

    public static void handle(PossessionMoveC2SPacket payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.player().getServer();

        if ( server == null ) return;

        server.execute(() -> {
            ServerPlayerEntity player = context.player();

            MobEntity entity = ((PossessionInterface) (Object) player).lesbos$getPossessedEntity();
            if (entity == null) return;

            entity.move(MovementType.PLAYER, new Vec3d(payload.x, payload.y, payload.z).subtract(entity.getPos()));
            entity.updatePositionAndAngles(payload.x, payload.y, payload.z, payload.yaw, payload.pitch);
        });
    }
}