package morgan.lebois.interfaces;

import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public interface PossessionInterface {
    public boolean lebois$isPossessing();
    public @Nullable MobEntity lebois$getPossessedEntity();
    public void lebois$setPossessedEntity(@Nullable MobEntity entity);

    public boolean lebois$canPossess(MobEntity entity);
    public boolean lebois$possess(MobEntity entity);
    public void lebois$unPossess();
}
