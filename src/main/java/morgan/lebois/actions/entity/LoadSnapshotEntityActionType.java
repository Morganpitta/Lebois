package morgan.lebois.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lebois.actions.LeboisActionTypes;
import morgan.lebois.cardinalComponents.LeboisEntityComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class LoadSnapshotEntityActionType extends EntityActionType {
    public final String name;
    public final boolean clear;

    public static final TypedDataObjectFactory<LoadSnapshotEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("name", SerializableDataTypes.STRING)
                    .add("clear", SerializableDataTypes.BOOLEAN, false),
            data -> new LoadSnapshotEntityActionType(
                    data.getString("name"),
                    data.getBoolean("clear")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("name", actionType.name)
                    .set("clear", actionType.clear)
    );

    public LoadSnapshotEntityActionType(String name, boolean clear) {
        this.name = name;
        this.clear = clear;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();

        if (entity instanceof LivingEntity) {
            entity.getComponent(LeboisEntityComponents.SNAPSHOT).loadSnapshot(this.name, clear);
        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LeboisActionTypes.LOAD_SNAPSHOT;
    }
}