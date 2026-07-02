package morgan.lebois.mixin.effects.entity;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import morgan.lebois.Lebois;
import morgan.lebois.common.Util;
import morgan.lebois.entity.effect.LeboisStatusEffects;
import morgan.lebois.interfaces.EffectSource;
import morgan.lebois.world.explosion.UnstableExplosionBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EffectSource {
    @Unique
    private final Map<RegistryEntry<StatusEffect>, Source> statusEffectsSources = Maps.newHashMap();

    @Unique
    private static final Codec<Map<RegistryEntry<StatusEffect>, Source>> SOURCES_CODEC =
            Codec.unboundedMap(
                    Registries.STATUS_EFFECT.getEntryCodec(),
                    Source.CODEC
            );

    @Shadow
    public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow
    public abstract @Nullable StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow
    public abstract boolean isDead();

    @Shadow
    public int deathTime;

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld serverWorld && this.hasStatusEffect(LeboisStatusEffects.FALTERED) && this.age % 2 == 0) {
            serverWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, this.getX(), this.getBodyY(1) + 0.25, this.getZ(), 0, 0, -1.0, 0, 0.1);
        }
    }

    public @Nullable EffectSource.Source lebois$getStatusEffectSource(RegistryEntry<StatusEffect> statusEffect) {
        return this.statusEffectsSources.get(statusEffect);
    }

    public void lebois$setStatusEffectSource(RegistryEntry<StatusEffect> statusEffect, @Nullable EffectSource.Source source) {
        if (source == null) {
            this.statusEffectsSources.remove(statusEffect);
        } else {
            this.statusEffectsSources.put(statusEffect, source);
        }
    }

    @Unique
    public void triggerUnstableExplosion(Entity attacker, float damage, int amplifier) {
        if (this.getWorld() instanceof ServerWorld serverWorld && attacker != null) {
            serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 1.5F);

            serverWorld.createExplosion(
                    null,
                    this.getDamageSources().explosion(this, attacker),
                    new UnstableExplosionBehavior(attacker, damage * (amplifier + 1)),
                    this.getX(), this.getY(), this.getZ(),
                    4,
                    false,
                    World.ExplosionSourceType.MOB
            );

            Util.spawnExpandingSphericalParticles(serverWorld, ParticleTypes.SOUL_FIRE_FLAME, new Vec3d(this.getX(), this.getBodyY(0.5), this.getZ()), 18, 18, 0.5);
        }
    }

    @Inject(method = "damage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z")))
    private void applyUnstableOnHit(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }

        if (this.getWorld() instanceof ServerWorld) {
            Entity attacker = damageSource.getAttacker();

            if (attacker instanceof LivingEntity livingEntity && damageSource.getSource() != this) {
                StatusEffectInstance effect = livingEntity.getStatusEffect(LeboisStatusEffects.OVERCHARGED);
                if (effect != null) {
                    StatusEffectInstance unstable = new StatusEffectInstance(LeboisStatusEffects.UNSTABLE, 40, effect.getAmplifier());
                    this.lebois$setStatusEffectSource(LeboisStatusEffects.UNSTABLE, new Source(attacker.getUuid(), amount));
                    this.addStatusEffect(unstable);

                    // Clear it down to zero ticks
                    if (effect.getDuration() > 0) {
                        livingEntity.removeStatusEffect(LeboisStatusEffects.OVERCHARGED);
                        livingEntity.addStatusEffect(new StatusEffectInstance(
                                LeboisStatusEffects.OVERCHARGED,
                                0,
                                effect.getAmplifier()
                        ));
                    }
                }
            }
        }
    }

    @Inject(method = "updatePostDeath", at=@At("HEAD"))
    public void updatePostDeath(CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld serverWorld && this.deathTime == 0) {
            StatusEffectInstance effect = this.getStatusEffect(LeboisStatusEffects.UNSTABLE);
            Source source = this.lebois$getStatusEffectSource(LeboisStatusEffects.UNSTABLE);

            if (effect != null && source != null && source.attackerUuid() != null) {
                Entity effectAttacker = serverWorld.getEntity(source.attackerUuid());

                if (effectAttacker instanceof LivingEntity livingEntity) {
                    // In case the zero tick hasn't been removed yet, do it ourselves
                    StatusEffectInstance attackerEffect = livingEntity.getStatusEffect(LeboisStatusEffects.OVERCHARGED);
                    if (attackerEffect != null && attackerEffect.getDuration() == 0) {
                        livingEntity.removeStatusEffect(LeboisStatusEffects.OVERCHARGED);
                    }

                    this.triggerUnstableExplosion(effectAttacker, source.damage(), effect.getAmplifier());
                }
            }
        }
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("TAIL"))
    public void clearStatusEffectSource(StatusEffectInstance effect, CallbackInfo ci) {
        Source source = this.lebois$getStatusEffectSource(effect.getEffectType());
        if (source != null) {
            this.lebois$setStatusEffectSource(effect.getEffectType(), null);
        }
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("HEAD"))
    public void onUnstableFinished(StatusEffectInstance effect, CallbackInfo ci) {
        if (!this.isDead()) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                if (effect.equals(LeboisStatusEffects.UNSTABLE)) {
                    Source source = this.lebois$getStatusEffectSource(LeboisStatusEffects.UNSTABLE);
                    if (source != null && source.attackerUuid() != null) {
                        Entity attacker = serverWorld.getEntity(source.attackerUuid());
                        this.triggerUnstableExplosion(attacker, source.damage(), effect.getAmplifier());
                    }
                }
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!this.statusEffectsSources.isEmpty()) {
            String key = Lebois.stringId("status_effects_sources");

            SOURCES_CODEC.encodeStart(NbtOps.INSTANCE, this.statusEffectsSources).resultOrPartial(Lebois.LOGGER::error).ifPresent(nbtElement -> nbt.put(key, nbtElement));
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void loadSourceFromNbt(NbtCompound nbt, CallbackInfo ci) {
        String key = Lebois.stringId("status_effects_sources");

        if (nbt.contains(key, NbtElement.COMPOUND_TYPE)) {
            SOURCES_CODEC.parse(NbtOps.INSTANCE, nbt.get(key)).resultOrPartial(Lebois.LOGGER::error).ifPresent(this.statusEffectsSources::putAll);
        }
    }
}
