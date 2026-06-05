package morgan.lesbos.common;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Util {
    public static List<Entity> getEntitiesInCone(ServerWorld world, Entity except, Vec3d origin, Vec3d direction, double distance, double coneAngle) {
        Box searchBox = new Box(origin, origin).expand(distance);

        double cosHalfAngle = Math.cos(Math.toRadians(coneAngle / 2.0));
        Vec3d directionNormalised = direction.normalize();

        return world.getOtherEntities(except, searchBox).stream()
                .filter(entity -> {
                    Vec3d vectorToEntity = entity.getPos().subtract(origin);
                    double distSq = vectorToEntity.lengthSquared();

                    if (distSq > distance * distance) return false;

                    if (distSq < 0.0001) {
                        return true;
                    }

                    double dotProduct = directionNormalised.dotProduct(vectorToEntity.normalize());

                    return dotProduct >= cosHalfAngle;
                }).toList();
    }

    public static <T extends ParticleEffect> int spawnParticles(ServerWorld world, T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, false, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        int i = 0;

        for (int j = 0; j < world.getPlayers().size(); j++) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)world.getPlayers().get(j);
            if (world.sendToPlayerIfNearby(serverPlayerEntity, force, x, y, z, particleS2CPacket)) {
                i++;
            }
        }

        return i;
    }
}
