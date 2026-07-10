package morgan.lebois.entity;

import morgan.lebois.Lebois;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LeboisEntityType {
    public static final EntityType<GrappleHookEntity> GRAPPLE_HOOK = register(
            "grapple_hook",
            EntityType.Builder.<GrappleHookEntity>create(GrappleHookEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(16)
                    .trackingTickInterval(10)
    );

    public static final EntityType<CoinEntity> COIN = register(
            "coin",
            EntityType.Builder.<CoinEntity>create(CoinEntity::new, SpawnGroup.MISC)
            .dimensions(0.25F, 0.25F)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
    );

    public static final EntityType<CloneEntity> CLONE = register(
            "clone",
            EntityType.Builder.<CloneEntity>create(CloneEntity::new, SpawnGroup.MISC)
                    .dimensions(0.6F, 1.8F)
                    .eyeHeight(1.62F)
                    .maxTrackingRange(8)
                    .trackingTickInterval(3)
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(CLONE, CloneEntity.createAttributes());
    }

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, Lebois.id(path),builder.build());
    }
}