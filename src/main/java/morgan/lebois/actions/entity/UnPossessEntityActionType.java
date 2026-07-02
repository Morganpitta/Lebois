package morgan.lebois.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import morgan.lebois.actions.LeboisActionTypes;
import morgan.lebois.interfaces.PossessionInterface;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class UnPossessEntityActionType extends EntityActionType {
    public static final TypedDataObjectFactory<UnPossessEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData(),
            data -> new UnPossessEntityActionType(),
            (actionType, serializableData) -> serializableData.instance()
    );

    public UnPossessEntityActionType() {
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof ServerPlayerEntity)) {
            return;
        }

        PossessionInterface player = (PossessionInterface) context.entity();

        player.lebois$unPossess();
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LeboisActionTypes.UN_POSSESS;
    }
}