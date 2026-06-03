package morgan.lesbos.network.packet;

import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.PossessionInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class LesbosClientPackets {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PossessionS2CPacket.ID, LesbosClientPackets::handlePossessionPacket);
        ClientPlayNetworking.registerGlobalReceiver(UnPossessionS2CPacket.ID, LesbosClientPackets::handleUnPossessionPacket);
    }

    public static void handlePossessionPacket(PossessionS2CPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> tryPossess(context.player(), payload.entityId(), 20));
    }

    private static void tryPossess(PlayerEntity player, int entityId, int retries) {
        if (player == null || player.getWorld() == null) return;

        Entity entity = player.getWorld().getEntityById(entityId);

        if (entity instanceof MobEntity mob) {
            ((PossessionInterface) player).lesbos$possess(mob);
        }
        else {
            if (retries > 0) {
                MinecraftClient.getInstance().execute(() -> {
                    tryPossess(player, entityId, retries - 1);
                });
            }
            else {
                Lesbos.LOGGER.warn("Failed to possess entity ID {}, entity was never found", entityId);
            }
        }
    }

    public static void handleUnPossessionPacket(UnPossessionS2CPacket payload, ClientPlayNetworking.Context context) {
        PlayerEntity player = context.player();

        ((PossessionInterface) player).lesbos$unPossess();
    }
}