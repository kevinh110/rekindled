package edu.cornell.gdiac.rekindled;

public class Player extends Entity {
    public int lights;
    private boolean cooldown;

    private static final int MAXLIGHTS = 2;

    public Player(float x, float y, float speed, Direction direction){
        super(x, y, speed, direction);
        lights = 0;
    }
    public Player(float x, float y, float speed){
        this(x, y, speed, Direction.RIGHT);
    }

    public void decreaseLights() {
        lights--;
    }

    public void increaseLights() {
        lights++;
    }

    public boolean hasSpace() {
        return lights < MAXLIGHTS;
    }

    public boolean hasLights() {
        return lights > 0;
    }

    public boolean hasLightRadius() {
        return lights > 0;
    }

    public void setLights(int x) {
        lights = x;
    }

    public void setCooldown(boolean value) { this.cooldown = value;}

    public boolean getCooldown() { return this.cooldown; }

}