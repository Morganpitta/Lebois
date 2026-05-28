package morgan.lesbos.mixin.entity.mob;

import morgan.lesbos.interfaces.GrappleInterface;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements PossessorInterface {
    @Unique
    @Nullable
    private PlayerEntity possessor;

    @Unique
    private static final TrackedData<Integer> POSSESSOR_ID = DataTracker.registerData(
            MobEntity.class, TrackedDataHandlerRegistry.INTEGER
    );

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Nullable
    public PlayerEntity lesbos$getPossessor() {
        return this.possessor;
    }

    public void lesbos$setPossessor(@Nullable PlayerEntity player) {
        this.possessor = player;
        this.dataTracker.set(POSSESSOR_ID, player != null ? player.getId() : -1);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTrackerAddPossessor(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(POSSESSOR_ID, -1);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSSESSOR_ID.equals(data)) {
            int id = this.dataTracker.get(POSSESSOR_ID);
            this.possessor = id != -1 ? (PlayerEntity) this.getWorld().getEntityById(id) : null;
            if ( this.possessor != null && ((PossessionInterface) this.possessor).lesbos$getPossessedEntity() != (MobEntity) (Object) this) this.possessor = null;
        }
        super.onTrackedDataSet(data);
    }
}
