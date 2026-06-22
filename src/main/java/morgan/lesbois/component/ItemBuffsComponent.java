package morgan.lesbois.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemBuffsComponent {
    public record ItemBuff(long expiryTick) {
        public static final Codec<ItemBuff> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.LONG.fieldOf("expiry_tick").forGetter(ItemBuff::expiryTick)
                ).apply(instance, ItemBuff::new)
        );

        public static final PacketCodec<RegistryByteBuf, ItemBuff> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_LONG, ItemBuff::expiryTick,
                ItemBuff::new
        );

        public boolean expired(long tick) {
            return expiryTick < tick;
        }
    }

    public static final ItemBuffsComponent DEFAULT = new ItemBuffsComponent(Map.of());

    public static final Codec<ItemBuffsComponent> CODEC =
        Codec.unboundedMap(Codec.STRING, ItemBuff.CODEC)
            .xmap(ItemBuffsComponent::new, component -> component.buffs);

    public static final PacketCodec<RegistryByteBuf, ItemBuffsComponent> PACKET_CODEC =
        PacketCodecs.map(HashMap::new, PacketCodecs.STRING, ItemBuff.PACKET_CODEC)
            .xmap(ItemBuffsComponent::new, component -> new HashMap<>(component.buffs));

    final Map<String, ItemBuff> buffs;

    ItemBuffsComponent(Map<String, ItemBuff> buffs) {
        this.buffs = Map.copyOf(buffs);
    }

    @Nullable
    public ItemBuff getBuff(String name) {
        return this.buffs.get(name);
    }

    public Map<String, ItemBuff> getBuffs() {
        return this.buffs;
    }

    public String toString() {
        return "ItemBuffs{" + this.buffs.toString() + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static class Builder {
        private final Map<String, ItemBuff> buffs = new HashMap<>();

        public Builder() {
        }

        public Builder(ItemBuffsComponent existing) {
            this.buffs.putAll(existing.getBuffs());
        }

        public Builder add(String name, ItemBuff buff) {
            this.buffs.put(name, buff);
            return this;
        }

        public Builder remove(String name) {
            this.buffs.remove(name);
            return this;
        }

        public ItemBuffsComponent build() {
            return new ItemBuffsComponent(this.buffs);
        }
    }
}
