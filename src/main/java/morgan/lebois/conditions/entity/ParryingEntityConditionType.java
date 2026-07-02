package morgan.lebois.conditions.entity;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import morgan.lebois.conditions.LeboisConditionTypes;
import morgan.lebois.interfaces.Parry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class ParryingEntityConditionType extends EntityConditionType {
    @Override
    public boolean test(EntityConditionContext context) {
        Entity entity = context.entity();
        if ( entity instanceof PlayerEntity) {
            return ((Parry) entity).lebois$isParrying();
        }
        else {
            return false;
        }
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return LeboisConditionTypes.PARRYING;
    }
}
