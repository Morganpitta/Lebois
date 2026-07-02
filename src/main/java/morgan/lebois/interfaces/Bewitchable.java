package morgan.lebois.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.EntityView;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Bewitchable {
    @Nullable
    UUID lebois$getOwnerUuid();
    void lebois$setOwnerUuid(@Nullable UUID uuid);

    EntityView getWorld();

    @Nullable
    default PlayerEntity lebois$getOwner() {
        UUID uuid = this.lebois$getOwnerUuid();
        return uuid == null ? null : this.getWorld().getPlayerByUuid(uuid);
    }

    default boolean lebois$isBewitched() {
        return this.lebois$getOwnerUuid() != null;
    }

    void lebois$setBewitched(@Nullable PlayerEntity owner);
}
