package morgan.lebois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WingsPowerType extends PowerType {
    private final float acceleration;
    private final float maxSpeed;
    private final float boost;
    private final Identifier texture;

    public static final TypedDataObjectFactory<WingsPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("acceleration", SerializableDataTypes.FLOAT, 0.15F)
                    .add("max_speed", SerializableDataTypes.FLOAT, 1.0F)
                    .add("boost", SerializableDataTypes.FLOAT, 0.035F)
                    .add("texture", SerializableDataTypes.IDENTIFIER),
            (data, condition) -> new WingsPowerType(
                    data.get("acceleration"),
                    data.get("max_speed"),
                    data.get("boost"),
                    data.get("texture"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("acceleration", powerType.acceleration)
                    .set("max_speed", powerType.maxSpeed)
                    .set("boost", powerType.boost)
                    .set("texture", powerType.texture)
    );

    WingsPowerType(float acceleration, float maxSpeed, float boost, Identifier texture, Optional<EntityCondition> condition) {
        super(condition);
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
        this.boost = boost;
        this.texture = texture;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LeboisPowerTypes.WINGS;
    }

    public static boolean hasWings(PlayerEntity player) {
        return PowerHolderComponent.hasPowerType (player, WingsPowerType.class);
    }

    public static float getAcceleration(PlayerEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.acceleration).max().orElse(0);
    }

    public static float getMaxSpeed(PlayerEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.maxSpeed).max().orElse(0);
    }

    public static float getBoost(PlayerEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.boost).max().orElse(0);
    }

    public static Identifier getTexture(PlayerEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .map(powerType -> powerType.texture).findFirst().orElse(null);
    }
}