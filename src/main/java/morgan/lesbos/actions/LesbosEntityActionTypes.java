package morgan.lesbos.actions;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.action.type.EntityActionTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import morgan.lesbos.Lesbos;
import morgan.lesbos.actions.entity.GrappleEntityActionType;
import morgan.lesbos.actions.entity.UnGrappleEntityActionType;

public class LesbosEntityActionTypes {
    public static final ActionConfiguration<GrappleEntityActionType> GRAPPLE = register("grapple", GrappleEntityActionType.DATA_FACTORY);
    public static final ActionConfiguration<UnGrappleEntityActionType> UN_GRAPPLE = register("un_grapple", UnGrappleEntityActionType.DATA_FACTORY);

    public static void register() {
    }

    public static <T extends EntityActionType> ActionConfiguration<T> register(String path, TypedDataObjectFactory<T> dataFactory) {
        ActionConfiguration<T> configuration = ActionConfiguration.of(Lesbos.id(path), dataFactory);

        EntityActionTypes.register(configuration);

        return configuration;
    }
}
