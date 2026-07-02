package morgan.lebois.conditions;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.condition.type.EntityConditionTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import morgan.lebois.Lebois;
import morgan.lebois.conditions.entity.GrapplingEntityConditionType;
import morgan.lebois.conditions.entity.ParryingEntityConditionType;
import morgan.lebois.conditions.entity.PossessingEntityEntityConditionType;

import java.util.function.Supplier;

public class LeboisConditionTypes {
    public static final ConditionConfiguration<GrapplingEntityConditionType> GRAPPLING = register("grappling", GrapplingEntityConditionType::new);
    public static final ConditionConfiguration<PossessingEntityEntityConditionType> POSSESSING_ENTITY = register("possessing_entity", PossessingEntityEntityConditionType.DATA_FACTORY);
    public static final ConditionConfiguration<ParryingEntityConditionType> PARRYING = register("parrying", ParryingEntityConditionType::new);

    public static void register() {
    }

    public static <T extends EntityConditionType> ConditionConfiguration<T> register(String path, TypedDataObjectFactory<T> dataFactory) {
        ConditionConfiguration<T> configuration = ConditionConfiguration.of(Lebois.id(path), dataFactory);

        EntityConditionTypes.register(configuration);

        return configuration;
    }

    public static <T extends EntityConditionType> ConditionConfiguration<T> register(String path, Supplier<T> constructor) {
        ConditionConfiguration<T> configuration = ConditionConfiguration.simple(Lebois.id(path), constructor);

        EntityConditionTypes.register(configuration);

        return configuration;
    }
}