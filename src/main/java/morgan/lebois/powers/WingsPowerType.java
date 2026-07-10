package morgan.lebois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WingsPowerType extends PowerType {
    private final float acceleration;
    private final float maxSpeed;
    private final int maxUseTime;
    private final float boost;
    private final Identifier texture;
    private final int width;
    private final int height;
    private final float scale;
    private final float offset;

    public static final TypedDataObjectFactory<WingsPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("acceleration", SerializableDataTypes.FLOAT, 0.15F)
                    .add("max_speed", SerializableDataTypes.FLOAT, 1.0F)
                    .add("max_use_time", SerializableDataTypes.INT, -1)
                    .add("boost", SerializableDataTypes.FLOAT, 0.035F)
                    .add("texture", SerializableDataTypes.IDENTIFIER)
                    .add("width", SerializableDataTypes.INT)
                    .add("height", SerializableDataTypes.INT)
                    .add("scale", SerializableDataTypes.FLOAT, 1.0F)
                    .add("offset", SerializableDataTypes.FLOAT, 0.0F),
            (data, condition) -> new WingsPowerType(
                    data.get("acceleration"),
                    data.get("max_speed"),
                    data.get("max_use_time"),
                    data.get("boost"),
                    data.get("texture"),
                    data.get("width"),
                    data.get("height"),
                    data.get("scale"),
                    data.get("offset"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("acceleration", powerType.acceleration)
                    .set("max_speed", powerType.maxSpeed)
                    .set("max_use_time", powerType.maxUseTime)
                    .set("boost", powerType.boost)
                    .set("texture", powerType.texture)
                    .set("width", powerType.width)
                    .set("height", powerType.height)
                    .set("scale", powerType.scale)
                    .set("offset", powerType.offset)
    );

    WingsPowerType(float acceleration, float maxSpeed, int maxUseTime, float boost, Identifier texture, int width, int height, float scale, float offset, Optional<EntityCondition> condition) {
        super(condition);
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
        this.maxUseTime = maxUseTime;
        this.boost = boost;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.offset = offset;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LeboisPowerTypes.WINGS;
    }

    public static boolean hasWings(LivingEntity player) {
        return PowerHolderComponent.hasPowerType (player, WingsPowerType.class);
    }

    public static float getAcceleration(LivingEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.acceleration).max().orElse(0);
    }

    public static float getMaxSpeed(LivingEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.maxSpeed).max().orElse(0);
    }

    public static int getMaxUseTime(LivingEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToInt(powerType -> powerType.maxUseTime).max().orElse(0);
    }

    public static float getBoost(LivingEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.boost).max().orElse(0);
    }

    public static Identifier getTexture(LivingEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .map(powerType -> powerType.texture).findFirst().orElse(null);
    }

    public static int getWidth(LivingEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .map(powerType -> powerType.width).findFirst().orElse(0);
    }

    public static int getHeight(LivingEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .map(powerType -> powerType.height).findFirst().orElse(0);
    }

    public static float getScale(LivingEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .map(powerType -> powerType.scale).findFirst().orElse(1.0F);
    }

    public static float getOffset(LivingEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .map(powerType -> powerType.offset).findFirst().orElse(0.0F);
    }
}