package morgan.lebois.interfaces;

import morgan.lebois.entity.GrappleHookEntity;
import org.jetbrains.annotations.Nullable;

public interface Grapple {
    public GrappleHookEntity lebois$getGrappleHook();
    public void lebois$setGrappleHook(@Nullable GrappleHookEntity hook);
    public GrappleHookEntity lebois$grapple(float maxDistance, float minDistance, boolean disableFallDamage, float pullSpeed, float lookAssist, float damping);
    public boolean lebois$unGrapple();
}
