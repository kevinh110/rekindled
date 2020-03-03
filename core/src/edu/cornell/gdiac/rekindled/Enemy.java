package edu.cornell.gdiac.rekindled;

public class Enemy extends Entity {
    boolean isLit;
    public Enemy(float x, float y, float speed, Direction direction){
        super(x, y, speed, direction);
        isLit = false;
    }

    public Enemy(float x, float y, float speed){
        super(x, y, speed, Direction.RIGHT);
        isLit = false;
    }

    public boolean getIsLit(){
        return isLit;
    }

    public void setIsLit(boolean value){
        isLit = value;
        if(isLit){
            this.setMainState(false);
        }
        else{
            this.setMainState(true);
        }
    }
}