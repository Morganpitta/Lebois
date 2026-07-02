package morgan.lebois.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PossessorInterface {
    public void lebois$setPossessor(@Nullable PlayerEntity player);
    public @Nullable PlayerEntity lebois$getPossessor();

    public void lebois$stopTargetSelectorGoals();
}
