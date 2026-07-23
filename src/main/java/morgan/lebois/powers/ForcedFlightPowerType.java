package morgan.lebois.powers;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.CreativeFlightPowerType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ForcedFlightPowerType extends CreativeFlightPowerType {
    public ForcedFlightPowerType(Optional<EntityCondition> condition) {
        super(condition);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LeboisPowerTypes.FORCED_FLIGHT;
    }
}
