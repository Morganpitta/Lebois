package morgan.lebois.mixin.wings.entity;

import morgan.lebois.interfaces.Winged;
import morgan.lebois.powers.WingsPowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Winged {
    @Shadow
    public float forwardSpeed;
    @Shadow
    public float sidewaysSpeed;
    @Unique
    private static final float WING_ANGLE_ACCELERATION = 0.2F;
    @Unique
    private static final float WING_DISTANCE_ACCELERATION = 0.1F;
    @Unique
    private static final TrackedData<Boolean> IS_FLYING = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique private int flyingTime = 0;
    @Unique private float wingSpeed = 0.0F;
    @Unique private float wingAngle = 0.0F;
    @Unique private float prevWingAngle = 0.0F;
    @Unique private float wingDistance = 0.0F;
    @Unique private float prevWingDistance = 0.0F;

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
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

    public int lebois$getFlyingTime() {
        return this.flyingTime;
    }

    public void lebois$setFlyingTime(int value) {
        this.flyingTime = value;
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
        if (WingsPowerType.hasWings((LivingEntity) (Object) this)) {
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

    public boolean lebois$gliding() {
        int maxUseTime = WingsPowerType.getMaxUseTime((LivingEntity) (Object) this);
        return maxUseTime != -1 && this.flyingTime >= maxUseTime;
    }

    @Inject(method = "tickMovement", at=@At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (WingsPowerType.hasWings((LivingEntity) (Object) this)) {
            if (this.lebois$isFlying()){
                float acceleration = WingsPowerType.getAcceleration((LivingEntity) (Object) this);
                float maxSpeed = WingsPowerType.getMaxSpeed((LivingEntity) (Object) this);
                float boost = WingsPowerType.getBoost((LivingEntity) (Object) this);

                float yaw = this.getYaw() * ((float)Math.PI / 180F);

                Vec3d directionNormalised = new Vec3d(
                        (-MathHelper.sin(yaw) * this.forwardSpeed) + (MathHelper.cos(yaw) * this.sidewaysSpeed),
                        0,
                        (MathHelper.cos(yaw) * this.forwardSpeed) + (MathHelper.sin(yaw) * this.sidewaysSpeed)
                ).normalize();

                this.setVelocity(this.getVelocity().add(directionNormalised.x * boost, 0.0F, directionNormalised.z * boost));

                double yVelocity = this.getVelocity().y;
                if (!this.lebois$gliding()) {
                    if (yVelocity < maxSpeed) {
                        yVelocity = Math.min(maxSpeed, yVelocity + acceleration);
                    }
                }
                else {
                    if (yVelocity < 0) {
                        yVelocity *= 0.85;
                    }
                }

                this.setVelocity(this.getVelocity().x, yVelocity, this.getVelocity().z);

                this.flyingTime++;

                this.fallDistance = 0;
                this.velocityDirty = true;
            }
        }
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    public void tickWings(CallbackInfo ci) {
        this.lebois$updateWings();
    }
}
