package morgan.lebois.interfaces;

public interface Winged {
    public boolean lebois$isFlying();
    public void lebois$setFlying(boolean value);

    public int lebois$getFlyingTime();
    public void lebois$setFlyingTime(int value);

    public float lebois$getWingAngle();
    public float lebois$getPrevWingAngle();
    public float lebois$getWingDistance();
    public float lebois$getPrevWingDistance();
    public void lebois$updateWings();

    public boolean lebois$canPropell();
}