package morgan.lebois;

import morgan.lebois.actions.LeboisActionTypes;
import morgan.lebois.block.LeboisBlocks;
import morgan.lebois.command.LeboisCommands;
import morgan.lebois.component.LeboisComponentTypes;
import morgan.lebois.conditions.LeboisConditionTypes;
import morgan.lebois.entity.LeboisEntityType;
import morgan.lebois.entity.effect.LeboisStatusEffects;
import morgan.lebois.item.LeboisItems;
import morgan.lebois.network.packet.LeboisPackets;
import morgan.lebois.powers.LeboisPowerTypes;
import morgan.lebois.sound.LeboisSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lebois implements ModInitializer {
    public static final String MOD_ID = "lebois";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    public static String stringId(String path) {
        return id(path).toString();
    }

    @Override
    public void onInitialize() {
        // Minecraft registries
        LeboisPackets.register();
        LeboisEntityType.register();
        LeboisCommands.register();
        LeboisSounds.register();
        LeboisComponentTypes.register();
        LeboisStatusEffects.register();
        LeboisItems.register();
        LeboisBlocks.register();

        // Apoli registries
        LeboisPowerTypes.register();
        LeboisConditionTypes.register();
        LeboisActionTypes.register();

        LOGGER.info("Lebois initialised!!!!!");
    }
}
