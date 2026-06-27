package morgan.lesbois.powers;

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
    private final int speed;
    private final Identifier texture;

    public static final TypedDataObjectFactory<WingsPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("speed", SerializableDataTypes.INT, 10)
                    .add("texture", SerializableDataTypes.IDENTIFIER),
            (data, condition) -> new WingsPowerType(
                    data.get("speed"),
                    data.get("texture"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("speed", powerType.speed)
                    .set("texture", powerType.texture)
    );

    WingsPowerType(int speed, Identifier texture, Optional<EntityCondition> condition) {
        super(condition);
        this.speed = speed;
        this.texture = texture;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.WINGS;
    }

    public static boolean hasWings(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(player);

        if (component == null) return false;

        return component.getPowers(true).stream().anyMatch(power -> power.getType() instanceof WingsPowerType && power.isActive(player));
    }

    public static int getSpeed(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(player);

        if (component == null) return 0;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof WingsPowerType && power.isActive(player))
                .mapToInt(power -> ((WingsPowerType) power.getType()).speed).max().orElse(0);
    }

    public static Identifier getTexture(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(player);

        if (component == null) return null;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof WingsPowerType && power.isActive(player))
                .map(power -> ((WingsPowerType) power.getType()).texture).findFirst().orElse(null);
    }
}