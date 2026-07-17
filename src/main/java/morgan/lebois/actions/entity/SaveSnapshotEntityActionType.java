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

public class SaveSnapshotEntityActionType extends EntityActionType {
    public final String name;

    public static final TypedDataObjectFactory<SaveSnapshotEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("name", SerializableDataTypes.STRING),
            data -> new SaveSnapshotEntityActionType(
                    data.getString("name")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("name", actionType.name)
    );

    public SaveSnapshotEntityActionType(String name) {
        this.name = name;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();

        if (entity instanceof LivingEntity) {
            entity.getComponent(LeboisEntityComponents.SNAPSHOT).saveSnapshot(this.name);
        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LeboisActionTypes.SAVE_SNAPSHOT;
    }
}
