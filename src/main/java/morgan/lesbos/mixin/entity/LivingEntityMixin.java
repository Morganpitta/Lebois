package morgan.lesbos.mixin.entity;

import morgan.lesbos.components.LesbosComponents;
import morgan.lesbos.components.PossessionComponent;
import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.network.packet.DoubleJumpC2SPacket;
import morgan.lesbos.powers.DoubleJumpPowerType;
import morgan.lesbos.powers.DragModifierPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements DoubleJumpInterface {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyConstant(
            method = "travel",
            constant = @Constant(floatValue = 0.91F)
    )
    public float travelModifyAirDrag(float constant) {
        return DragModifierPowerType.getAirDrag((LivingEntity) (Object) this);
    }

    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void redirectHealth(CallbackInfoReturnable<Float> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null) {
                    cir.setReturnValue(entity.getHealth());
                }
            }
        }
    }

    @Inject(method = "getAttributeValue", at = @At("HEAD"), cancellable = true)
    private void redirectAttributes(RegistryEntry<EntityAttribute> attribute, CallbackInfoReturnable<Double> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null && entity.getAttributes().hasAttribute(attribute)) {
                    cir.setReturnValue(entity.getAttributeValue(attribute));
                }
            }
        }
    }
}
