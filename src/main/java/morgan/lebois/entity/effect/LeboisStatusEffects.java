package morgan.lebois.entity.effect;

import morgan.lebois.Lebois;
import net.minecraft.entity.effect.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class LeboisStatusEffects {
    public static final RegistryEntry<StatusEffect> FALTERED = register(
            "faltered",
            new StatusEffect(StatusEffectCategory.HARMFUL, 0x000000) {}
    );
    public static final RegistryEntry<StatusEffect> OVERCHARGED = register(
            "overcharged",
            new StackingStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00F0FF, 5)
    );
    public static final RegistryEntry<StatusEffect> UNSTABLE = register(
            "unstable",
            new StackingStatusEffect(StatusEffectCategory.HARMFUL, 0x00F0FF, 5)
    );

    public static void register() {}

    private static RegistryEntry<StatusEffect> register(String path, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Lebois.id(path), statusEffect);
    }
}
