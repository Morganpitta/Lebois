package morgan.lebois.cardinalComponents;

import morgan.lebois.Lebois;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SnapshotComponent implements Component {
    private final LivingEntity livingEntity;
    private final Map<String, Snapshot> snapshots = new HashMap<>();

    public SnapshotComponent(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public boolean hasSnapshot(String id) { return snapshots.containsKey(id); }

    public void saveSnapshot(String id) {
        this.snapshots.put(id, new Snapshot(
                livingEntity.getPos(),
                livingEntity.getVelocity(),
                livingEntity.getEntityWorld().getRegistryKey(),
                livingEntity.getYaw(),
                livingEntity.getPitch(),
                livingEntity.getHealth(),
                livingEntity.fallDistance
        ));
    }

    public void clearSnapshot(String id) {
        snapshots.remove(id);
    }

    public void loadSnapshot(String id, boolean clear) {
        Snapshot snapshot = snapshots.get(id);

        if (snapshot != null && livingEntity.getServer() != null) {
            ServerWorld world = livingEntity.getServer().getWorld(snapshot.worldKey);

            if (world != null) {
                livingEntity.teleport(world, snapshot.pos.x, snapshot.pos.y, snapshot.pos.z, Set.of(), snapshot.yaw, snapshot.pitch);
                livingEntity.setVelocity(snapshot.velocity);
                livingEntity.setHealth(snapshot.health);
                livingEntity.fallDistance = snapshot.fallDistance;
                livingEntity.extinguish();

                livingEntity.velocityDirty = true;
                livingEntity.velocityModified = true;
            }

            if (clear) this.clearSnapshot(id);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        snapshots.clear();

        if (tag.contains(Lebois.stringId("snapshots"))) {
            NbtCompound nbt = tag.getCompound(Lebois.stringId("snapshots"));

            for (String key : nbt.getKeys()) {
                this.snapshots.put(key, Snapshot.fromNbt(nbt.getCompound(key)));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (!this.snapshots.isEmpty()) {
            NbtCompound nbt = new NbtCompound();

            this.snapshots.forEach((id, snapshot) -> nbt.put(id, snapshot.toNbt()));

            tag.put(Lebois.stringId("snapshots"), nbt);
        }
    }

    private record Snapshot(
            Vec3d pos,
            Vec3d velocity,
            RegistryKey<World> worldKey,
            float yaw,
            float pitch,
            float health,
            float fallDistance
    ) {
        public static Snapshot fromNbt(NbtCompound nbt) {
            return new Snapshot(
                    new Vec3d(nbt.getDouble("posX"), nbt.getDouble("posY"), nbt.getDouble("posZ")),
                    new Vec3d(nbt.getDouble("velocityX"), nbt.getDouble("velocityY"), nbt.getDouble("velocityZ")),
                    RegistryKey.of(RegistryKeys.WORLD, Identifier.of(nbt.getString("worldKey"))),
                    nbt.getFloat("yaw"),
                    nbt.getFloat("pitch"),
                    nbt.getFloat("health"),
                    nbt.getFloat("fallDistance")
            );
        }

        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();

            nbt.putDouble("posX", this.pos.x);
            nbt.putDouble("posY", this.pos.y);
            nbt.putDouble("posZ", this.pos.z);

            nbt.putDouble("velocityX", this.velocity.x);
            nbt.putDouble("velocityY", this.velocity.y);
            nbt.putDouble("velocityZ", this.velocity.z);

            nbt.putString("worldKey", this.worldKey.getValue().toString());

            nbt.putFloat("yaw", this.yaw);
            nbt.putFloat("pitch", this.pitch);
            nbt.putFloat("health", this.health);
            nbt.putFloat("fallDistance", this.fallDistance);

            return nbt;
        }
    }
}
