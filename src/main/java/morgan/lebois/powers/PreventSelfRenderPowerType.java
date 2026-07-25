package morgan.lebois.powers;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PreventSelfRenderPowerType extends PowerType {
    public PreventSelfRenderPowerType(Optional<EntityCondition> condition) {
        super(condition);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LeboisPowerTypes.PREVENT_SELF_RENDER;
    }
}
