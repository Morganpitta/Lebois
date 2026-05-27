package morgan.lesbos.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DragModifierPowerType extends PowerType {
    private final float airDrag;
    private final boolean slideMode;

    public static final TypedDataObjectFactory<DragModifierPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("air_drag", SerializableDataTypes.FLOAT, 0.91F)
                    .add("slide_mode", SerializableDataTypes.BOOLEAN, false),
            (data, condition) -> new DragModifierPowerType(
                    data.get("air_drag"),
                    data.get("slide_mode"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("air_drag", powerType.airDrag)
                    .set("slide_mode", powerType.slideMode)
    );

    public DragModifierPowerType(float airDrag, boolean slideMode, Optional<EntityCondition> condition) {
        super(condition);
        this.airDrag = Math.clamp(airDrag, 0, 1);
        this.slideMode = slideMode;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesbosPowerTypes.DRAG_MODIFIER;
    }

    public float getAirDrag() {
        return this.airDrag;
    }

    public boolean getSlideMode() {
        return this.slideMode;
    }

    public static float getAirDrag(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if (component == null) return 0.91F;

        return (float) component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof DragModifierPowerType)
                .mapToDouble(power -> ((DragModifierPowerType) power.getType()).getAirDrag()).max()
                .orElse(0.91);
    }

    public static boolean hasSlideMode(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if (component == null) return false;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof DragModifierPowerType)
                .anyMatch(power -> ((DragModifierPowerType) power.getType()).getSlideMode());
    }
}
