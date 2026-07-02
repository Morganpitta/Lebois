package morgan.lebois.mixin.possession.entity.projectile;

import morgan.lebois.interfaces.PossessorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity {
    @Shadow
    public abstract @Nullable Entity getOwner();

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(
            method = "shouldLeaveOwner",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getRootVehicle()Lnet/minecraft/entity/Entity;"
            )
    )
    private Entity getRootVehicle(Entity entity) {
        if (entity instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) entity).lebois$getPossessor();

            if ( player != null ) {
                return player.getRootVehicle();
            }
        }

        return entity.getRootVehicle();
    }
}
