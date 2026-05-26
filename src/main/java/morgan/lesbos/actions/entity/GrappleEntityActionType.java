package morgan.lesbos.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbos.actions.LesbosEntityActionTypes;
import morgan.lesbos.interfaces.GrappleInterface;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class GrappleEntityActionType extends EntityActionType {
    private final double maxDistance;
    private final double minDistance;
    private final double speed;

    public static final TypedDataObjectFactory<GrappleEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("max_distance", SerializableDataTypes.DOUBLE, 20D)
                    .add("min_distance", SerializableDataTypes.DOUBLE, 2D)
                    .add("speed", SerializableDataTypes.DOUBLE, 1D),
            data -> new GrappleEntityActionType(
                    data.get("max_distance"),
                    data.get("min_distance"),
                    data.get("speed")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("max_distance", actionType.maxDistance)
                    .set("min_distance", actionType.minDistance)
                    .set("speed", actionType.speed)
    );

    public GrappleEntityActionType(double maxDistance, double minDistance, double speed) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.speed = speed;
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof ServerPlayerEntity)) {
            return;
        }

        GrappleInterface player = (GrappleInterface) context.entity();

        player.lesbos$grapple(this.maxDistance, this.minDistance, this.speed);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesbosEntityActionTypes.GRAPPLE;
    }
}