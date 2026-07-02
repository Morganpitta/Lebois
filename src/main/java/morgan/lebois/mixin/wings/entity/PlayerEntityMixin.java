package morgan.lebois.mixin.wings.entity;

import morgan.lebois.interfaces.Winged;
import morgan.lebois.powers.WingsPowerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Winged {
    @Unique
    private static final float WING_ANGLE_ACCELERATION = 0.2F;
    @Unique
    private static final float WING_DISTANCE_ACCELERATION = 0.1F;
    @Unique
    private static final TrackedData<Boolean> IS_FLYING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique float wingSpeed = 0.0F;
    @Unique float wingAngle = 0.0F;
    @Unique float prevWingAngle = 0.0F;
    @Unique float wingDistance = 0.0F;
    @Unique float prevWingDistance = 0.0F;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at=@At("TAIL"))
    public void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(IS_FLYING, false);
    }

    public boolean lebois$isFlying() {
        return this.dataTracker.get(IS_FLYING);
    }

    public void lebois$setFlying(boolean value) {
        this.dataTracker.set(IS_FLYING, value);
    }

    public float lebois$getWingAngle() {
        return this.wingAngle;
    }
    public float lebois$getPrevWingAngle() {
        return this.prevWingAngle;
    }
    public float lebois$getWingDistance() {
        return this.wingDistance;
    }
    public float lebois$getPrevWingDistance() {
        return this.prevWingDistance;
    }

    public void lebois$updateWings() {
        if (WingsPowerType.hasWings((PlayerEntity) (Object) this)) {
            float flapStrength = (float) Math.min(0.2F + new Vec3d(this.getX() - this.prevX, 0, this.getZ() - this.prevZ).horizontalLength() * 2.0F, 1.0F);

            if (this.lebois$isFlying()){
                flapStrength = 1.0F;
            }

            this.wingSpeed += (flapStrength * flapStrength - this.wingSpeed) * WING_ANGLE_ACCELERATION;

            this.prevWingAngle = this.wingAngle;
            this.wingAngle += this.wingSpeed;

            this.prevWingDistance = this.wingDistance;
            this.wingDistance += (flapStrength - this.wingDistance) * WING_DISTANCE_ACCELERATION;
        }
    }

    @Inject(method = "tickMovement", at=@At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (WingsPowerType.hasWings((PlayerEntity) (Object) this)) {
            float acceleration = WingsPowerType.getAcceleration((PlayerEntity) (Object) this);
            float maxSpeed = WingsPowerType.getMaxSpeed((PlayerEntity) (Object) this);
            float boost = WingsPowerType.getBoost((PlayerEntity) (Object) this);

            if (this.lebois$isFlying()){
                float yaw = this.getYaw() * ((float)Math.PI / 180F);

                Vec3d directionNormalised = new Vec3d(
                        (-MathHelper.sin(yaw) * this.forwardSpeed) + (MathHelper.cos(yaw) * this.sidewaysSpeed),
                        0,
                        (MathHelper.cos(yaw) * this.forwardSpeed) + (MathHelper.sin(yaw) * this.sidewaysSpeed)
                ).normalize();

                float clampedAcceleration = 0.0F;
                if (this.getVelocity().y < maxSpeed) {
                    clampedAcceleration = (float) Math.min(maxSpeed - this.getVelocity().y, acceleration);
                }

                this.setVelocity(this.getVelocity().add(directionNormalised.x * boost, clampedAcceleration, directionNormalised.z * boost));

                this.fallDistance = 0;
                this.velocityDirty = true;
            }
        }
    }
}
