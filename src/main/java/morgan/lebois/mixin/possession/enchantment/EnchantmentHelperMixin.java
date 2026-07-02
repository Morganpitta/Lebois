package morgan.lebois.mixin.possession.enchantment;

import morgan.lebois.interfaces.PossessorInterface;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyVariable(
            method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static LivingEntity forEachEnchantmentUsePossessor(LivingEntity entity) {
        if (entity instanceof MobEntity) {
            PlayerEntity owner = ((PossessorInterface) entity).lebois$getPossessor();

            if (owner != null) {
                return owner;
            }
        }

        return entity;
    }
}