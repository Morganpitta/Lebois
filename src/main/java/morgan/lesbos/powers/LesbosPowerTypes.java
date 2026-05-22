package morgan.lesbos.powers;

import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import morgan.lesbos.Lesbos;
import net.minecraft.registry.Registry;

public class LesbosPowerTypes {
    public static final PowerConfiguration<DoubleJumpPowerType> DOUBLE_JUMP = register(PowerConfiguration.of(Lesbos.id("double_jump"), DoubleJumpPowerType.DOUBLE_JUMP));

    public static void register() {
    }

    // https://github.com/apace100/origins-fabric/blob/1.13.0-pre.2%2Bmc.1.21.1/src/main/java/io/github/apace100/origins/power/type/OriginsPowerTypes.java
    public static <T extends PowerType> PowerConfiguration<T> register(PowerConfiguration<T> config) {
        //noinspection unchecked
        PowerConfiguration<PowerType> casted = (PowerConfiguration<PowerType>) config;
        Registry.register(ApoliRegistries.POWER_TYPE, casted.id(), casted);

        return config;
    }
}
