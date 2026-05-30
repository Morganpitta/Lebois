package morgan.lesbos.mixin.entity.player;

import morgan.lesbos.components.LesbosComponents;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityPossessionMixin extends LivingEntity implements PossessionInterface {
    protected PlayerEntityPossessionMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean lesbos$isPossessing() {
        return LesbosComponents.POSSESSION.get(this).isPossessing();
    }

    @Nullable
    public MobEntity lesbos$getPossessedEntity() {
        return LesbosComponents.POSSESSION.get(this).getPossessedEntity();
    }

    public void lesbos$setPossessedEntity(@Nullable MobEntity entity) {
        LesbosComponents.POSSESSION.get(this).setPossessedEntity(entity);
    }

    public void lesbos$possess(MobEntity entity) {
        if ( this.lesbos$getPossessedEntity() != null )
            this.lesbos$unPossess();

        this.lesbos$setPossessedEntity(entity);
        ((PossessorInterface) entity).lesbos$setPossessor((PlayerEntity) (Object) this);

        if (entity instanceof MobEntity mobEntity) {
            mobEntity.setAiDisabled(true);
        }

        this.setInvisible(true);
        this.noClip = true;
    }

    public void lesbos$unPossess() {
        MobEntity entity = this.lesbos$getPossessedEntity();

        if (entity != null ) {
            entity.setAiDisabled(false);

            ((PossessorInterface) entity).lesbos$setPossessor(null);
        }

        this.setInvisible(false);
        this.noClip = false;

        this.lesbos$setPossessedEntity(null);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        MobEntity entity = lesbos$getPossessedEntity();

        if (entity == null) return;

        if (entity.isDead() || entity.isRemoved()) {
            this.lesbos$unPossess();
            return;
        }

        if (((PossessorInterface) entity).lesbos$getPossessor() != (PlayerEntity) (Object) this) {
            ((PossessorInterface) entity).lesbos$setPossessor((PlayerEntity) (Object) this);
        }
    }


    // Health, hasStatusEffect, attributes
}
