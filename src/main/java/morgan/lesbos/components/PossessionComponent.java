package morgan.lesbos.components;

import morgan.lesbos.Lesbos;
import morgan.lesbos.mixin.common.client.world.ClientWorldAccessor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.UUID;

public class PossessionComponent implements AutoSyncedComponent {
    private final PlayerEntity playerEntity;

    @Nullable
    private UUID possessedEntityUuid;
    private MobEntity possessedEntity;

    public PossessionComponent(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public boolean isPossessing() {
        return this.possessedEntityUuid != null;
    }

    @Nullable
    public UUID getPossessedEntityUuid() {
        return this.possessedEntityUuid;
    }

    // Can return null even if entity exists, if the entity is in unloaded chunks...
    @Nullable
    private static MobEntity findEntityByUuid(World world, UUID uuid) {
        Entity possessedEntity = null;

        if (uuid==null) return null;

        if ( world instanceof ServerWorld serverWorld ) {
            possessedEntity = serverWorld.getEntity(uuid);
        }
        else if ( world instanceof ClientWorld clientWorld ) {
            possessedEntity = ((ClientWorldAccessor) clientWorld).lesbos$getEntityLookup().get(uuid);
        }

        return possessedEntity instanceof MobEntity entity ? entity : null;
    }

    @Nullable
    public MobEntity getPossessedEntity() {
        MobEntity entity = findEntityByUuid(this.playerEntity.getWorld(), this.possessedEntityUuid);

        if (entity != null && this.possessedEntity != entity) {
            this.possessedEntity = entity;
        }

        return this.possessedEntity;
    }

    public void setPossessedEntity(@Nullable MobEntity entity) {
        UUID uuid = entity == null ? null : entity.getUuid();
        if ( this.possessedEntityUuid != uuid ) {
            this.possessedEntityUuid = uuid;
            this.possessedEntity = entity;
            LesbosEntityComponents.POSSESSION.sync(this.playerEntity);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.containsUuid("lesbos:possession")) {
            this.possessedEntityUuid = tag.getUuid("lesbos:possession");
            this.possessedEntity = findEntityByUuid(this.playerEntity.getWorld(), this.possessedEntityUuid);
        }
        else {
            this.possessedEntityUuid = null;
            this.possessedEntity = null;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if( this.possessedEntityUuid != null ) {
            tag.putUuid("lesbos:possession", this.possessedEntityUuid);
        }
    }
}