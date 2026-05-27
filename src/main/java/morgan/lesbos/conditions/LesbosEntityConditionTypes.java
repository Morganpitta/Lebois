package morgan.lesbos.conditions;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.condition.type.EntityConditionTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import morgan.lesbos.Lesbos;

public class LesbosEntityConditionTypes {
    public static void register() {
    }

    public static <T extends EntityConditionType> ConditionConfiguration<T> register(String path, TypedDataObjectFactory<T> dataFactory) {
        ConditionConfiguration<T> configuration = ConditionConfiguration.of(Lesbos.id(path), dataFactory);

        EntityConditionTypes.register(configuration);

        return configuration;
    }
}