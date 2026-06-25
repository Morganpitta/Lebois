package morgan.lesbois.world.explosion;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class UnstableExplosionBehavior extends ExplosionBehavior {
    private final Entity attacker;
    private final float damage;

    public UnstableExplosionBehavior(Entity attacker, float damage) {
        this.attacker = attacker;
        this.damage = damage;
    }

    @Override
    public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        return false;
    }

    @Override
    public boolean shouldDamage(Explosion explosion, Entity entity) {
        return entity != attacker;
    }

    @Override
    public float calculateDamage(Explosion explosion, Entity entity) {
        float power = explosion.getPower() * 2.0F;
        Vec3d pos = explosion.getPosition();

        double distance = Math.sqrt(entity.squaredDistanceTo(pos)) / (power * 2.0F);
        double exposure = (1.0 - distance) * Explosion.getExposure(pos, entity);

        return (float)(((exposure * exposure + exposure) / 2.0F) * this.damage);
    }
}
