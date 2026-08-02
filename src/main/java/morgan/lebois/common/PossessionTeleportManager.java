package morgan.lebois.common;

public final class PossessionTeleportManager {
    private static boolean isPossessionTeleport = false;

    private PossessionTeleportManager() {}

    public static boolean isPossessionTeleport() {
        return isPossessionTeleport;
    }

    public static void setPossessionTeleport(boolean value) {
        isPossessionTeleport = value;
    }
}