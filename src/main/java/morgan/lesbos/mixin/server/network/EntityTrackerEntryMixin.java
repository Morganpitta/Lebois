package morgan.lesbos.mixin.server.network;

import morgan.lesbos.interfaces.PossessorInterface;
import net.fabricmc.fabric.mixin.networking.accessor.EntityTrackerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {

    @Shadow
    @Final
    private Entity entity;

    @Shadow
    @Final
    private Consumer<Packet<?>> receiver;

//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
//    public void acceptStop(Consumer<Object> instance, Object t) {
//        Entity entity = this.entity;
//        if (entity instanceof MobEntity mob && ((PossessorInterface) mob).lesbos$getPossessor() != null) {
//            if (packet instanceof EntityPositionS2CPacket ||
//                    packet instanceof EntityS2CPacket.MoveRelative ||
//                    packet instanceof EntityS2CPacket.RotateAndMoveRelative ||
//                    packet instanceof EntityVelocityUpdateS2CPacket) {
//                return; // skip position and velocity packets
//            }
//        }
//        consumer.accept((Packet<?>) packet);
//    }
}
