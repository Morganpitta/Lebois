package morgan.lebois.sound;

import morgan.lebois.Lebois;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class LeboisSounds {
    public static final SoundEvent SONIC_BOOM = register("sonic_boom");
    public static final SoundEvent PATRICK_SCREAM = register("patrick_scream");

    public static void register() {
    }

    private static SoundEvent register(String path) {
        return Registry.register(Registries.SOUND_EVENT, Lebois.id(path), SoundEvent.of(Lebois.id(path)));
    }
}