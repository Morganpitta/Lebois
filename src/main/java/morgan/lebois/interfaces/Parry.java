package morgan.lebois.interfaces;

import net.minecraft.entity.damage.DamageSource;

public interface Parry {
    public boolean lebois$canParry();
    public boolean lebois$isParrying();
    public void lebois$parry(DamageSource source, float amount);
    public boolean lebois$shouldRedirectProjectile();
    public void lebois$setRedirectProjectile(boolean value);
}
