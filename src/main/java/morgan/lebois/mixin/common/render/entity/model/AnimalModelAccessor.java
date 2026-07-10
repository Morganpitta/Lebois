package morgan.lebois.mixin.common.render.entity.model;

import net.minecraft.client.render.entity.model.AnimalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimalModel.class)
public interface AnimalModelAccessor {
    
    @Accessor("invertedChildBodyScale")
    float lebois$getInvertedChildBodyScale();

    @Accessor("childBodyYOffset")
    float lebois$getChildBodyYOffset();
}