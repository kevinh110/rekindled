package edu.cornell.gdiac.rekindled;

public class Constants {
    // Used in collision detecting
    public static final short BIT_PLAYER = 1;
    public static final short BIT_ENEMY = 2;
    public static final short BIT_WALL = 4;
    public static final short BIT_SOURCE = 8;

    // Used in player/enemy facing direction
    public static final int FORWARD = 1;
    public static final int BACK = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    // Maximum number of lights that Lux can carry
    public static final int MAX_LIGHTS = 10;

    // How far the enemies sight cone should extend
    public static final float SIGHT_CONE_RADIUS = 9f;

    // Defines the shape of the enemies sight cone (0..359.XX)
    public static final float SIGHT_CONE_SECTOR = 60f;

    // Defines how big the player's aura should be
    public static final float AURA_RADIUS = 3f;

    // Defines how big the light from a light source is
    public static final float SOURCE_LIGHT_RADIUS = 3f;

    // Defines how dark shadows should be (0..1)
    public static final float AMBIANCE = 0.2f;
}
