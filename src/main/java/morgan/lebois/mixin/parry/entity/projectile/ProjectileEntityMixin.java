package morgan.lebois.mixin.parry.entity.projectile;

import morgan.lebois.interfaces.Parry;
import morgan.lebois.interfaces.Parryable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity implements Parryable {
    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private Entity lebois$parriedOwner = null;

    public void lebois$setParriedOwner(Entity entity) {
        this.lebois$parriedOwner = entity;
    }

    @Inject(method = "canHit(Lnet/minecraft/entity/Entity;)Z", at=@At("HEAD"), cancellable = true)
    public void canHit(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity == lebois$parriedOwner) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean shouldLeaveParriedOwner() {
        Entity entity = this.lebois$parriedOwner;
        if (entity != null) {
            for (Entity entity2 : this.getWorld()
                    .getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()).expand(2), entityx -> !entityx.isSpectator() && entityx.canHit())) {
                if (entity2.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Inject(method = "tick", at=@At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.lebois$parriedOwner != null && this.shouldLeaveParriedOwner()) {
            this.lebois$parriedOwner = null;
        }
    }

    @Redirect(
            method = "deflect(Lnet/minecraft/entity/ProjectileDeflection;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ProjectileDeflection;deflect(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/random/Random;)V"
            )
    )
    private void deflectRedirectProjectile(ProjectileDeflection instance, ProjectileEntity projectile, Entity hitEntity, Random random) {
        if (hitEntity instanceof PlayerEntity && instance == ProjectileDeflection.SIMPLE) {
            if (((Parry) hitEntity).lebois$shouldRedirectProjectile()) {
                ((Parry) hitEntity).lebois$setRedirectProjectile(false);

                ProjectileDeflection.REDIRECTED.deflect(projectile, hitEntity, random);
                projectile.setVelocity(projectile.getVelocity().multiply(5)); // I hate that I have to include this but this is already way too much effort for something so simple
                ((Parryable) projectile).lebois$setParriedOwner(hitEntity);
                return;
            }
        }

        instance.deflect(projectile, hitEntity, random);
    }
}
