package morgan.lesbos.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class DoubleJumpComponent implements Component {
    private int doubleJumps = 0;

    public int getDoubleJumps() {
        return doubleJumps;
    }
    public void setDoubleJumps(int value) {
        this.doubleJumps = value;
    }
    public void addDoubleJumps(int amount) {
        this.doubleJumps += amount;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        doubleJumps = tag.getInt("lesbos:double_jumps");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("lesbos:double_jumps", doubleJumps);
    }
}