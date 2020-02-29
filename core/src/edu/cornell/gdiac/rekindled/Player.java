package edu.cornell.gdiac.rekindled;

public class Player extends Entity {
    public int lights;

    private static final int MAXLIGHTS = 2;

    public Player(float x, float y, float velocity, Direction direction){
        super(x, y, velocity, direction);
        lights = 0;
    }
    public Player(float x, float y, float velocity){
        this(x, y, velocity, Direction.RIGHT);
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


}
