package morgan.lebois.component;

import morgan.lebois.Lebois;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class LeboisComponentTypes {
//    Replacing this with a potion effect. Leaving code in for future reference
//    public static final ComponentType<ItemBuffsComponent> BUFFS = register(
//            "enchantments", builder -> builder.codec(ItemBuffsComponent.CODEC).packetCodec(ItemBuffsComponent.PACKET_CODEC).cache()
//    );

    public static void register() {
    }


    private static <T> ComponentType<T> register(String path, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Lebois.id(path), builderOperator.apply(ComponentType.builder()).build());
    }
}