package edu.cornell.gdiac.rekindled;

public class Enemy extends Entity {
    public Enemy(float x, float y, float vel, Direction direction){
        super(x, y, vel, direction);
    }

    public Enemy(float x, float y, float vel){
        super(x, y, vel, Direction.RIGHT);
    }
}