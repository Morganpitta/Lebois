package morgan.lesbos.powers;

import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.power.type.PowerTypes;
import morgan.lesbos.Lesbos;
import net.minecraft.util.Identifier;

public class LesbosPowerTypes {
    public static final PowerConfiguration<DoubleJumpPowerType> DOUBLE_JUMP = register("double_jump", DoubleJumpPowerType.DOUBLE_JUMP);

    public static void register() {
    }

    public static <T extends PowerType> PowerConfiguration<T> register(String path,  TypedDataObjectFactory<T> dataFactory) {
        PowerConfiguration<T> configuration = PowerConfiguration.of(Lesbos.id(path), dataFactory);

        PowerTypes.register(configuration);

        return configuration;
    }
}
